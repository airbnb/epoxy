package com.airbnb.epoxy.processor.resourcescanning

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.processor.containingPackage
import com.airbnb.epoxy.processor.resourcescanning.KspResourceScanner.ImportMatch.Normal
import com.airbnb.epoxy.processor.resourcescanning.KspResourceScanner.ImportMatch.TypeAlias
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.impl.java.KSAnnotationJavaImpl
import com.google.devtools.ksp.symbol.impl.java.KSClassDeclarationJavaImpl
import com.google.devtools.ksp.symbol.impl.kotlin.KSAnnotationImpl
import com.google.devtools.ksp.symbol.impl.kotlin.KSClassDeclarationImpl
import com.squareup.javapoet.ClassName
import org.jetbrains.kotlin.com.intellij.psi.PsiAnnotation
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaFile
import org.jetbrains.kotlin.com.intellij.psi.PsiNameValuePair
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCollectionLiteralExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.ValueArgument
import kotlin.reflect.KClass

class KspResourceScanner(environmentProvider: () -> XProcessingEnv) :
    ResourceScanner(environmentProvider) {
    private val cache =
        mutableMapOf<Pair<KClass<out Annotation>, XElement>, List<AnnotationWithReferenceValue>>()

    override fun getResourceValueListInternal(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
        values: List<Int>,
    ): List<ResourceValue> {
        val annotationArgs = getAnnotationArgs(annotation, element)

        return annotationArgs.filter { it.name == property }.mapNotNull { it.toResourceValue() }
    }

    override fun getResourceValueInternal(
        annotation: KClass<out Annotation>,
        element: XElement,
        property: String,
        value: Int
    ): ResourceValue? {
        val annotationArgs = getAnnotationArgs(annotation, element)

        val matchingArg = annotationArgs.firstOrNull { it.name == property } ?: return null

        return matchingArg.toResourceValue()
    }

    private fun getAnnotationArgs(
        annotation: KClass<out Annotation>,
        element: XElement
    ): List<AnnotationWithReferenceValue> {
        return cache.getOrPut(annotation to element) {
            val annotationBox = element.getAnnotation(annotation) ?: return@getOrPut emptyList()
            val ksAnnotation = annotationBox.getFieldWithReflection<KSAnnotation>("annotation")
            processAnnotationWithResource(ksAnnotation)
        }
    }

    override fun getImports(classElement: XTypeElement): List<String> {
        val ksClassDeclaration =
            classElement.getFieldWithReflection<KSClassDeclaration>("declaration")
        return when (ksClassDeclaration) {
            is KSClassDeclarationImpl -> {
                ksClassDeclaration.ktClassOrObject
                    .containingKtFile
                    .importDirectives
                    .mapNotNull { it.importPath?.toString() }
            }
            is KSClassDeclarationJavaImpl -> {
                (ksClassDeclaration.psi.containingFile as? PsiJavaFile)
                    ?.importList
                    ?.importStatements
                    ?.mapNotNull { it.qualifiedName }
                    ?: emptyList()
            }
            else -> emptyList()
        }
    }

    private fun processAnnotationWithResource(annotation: KSAnnotation): List<AnnotationWithReferenceValue> {
        val packageName = annotation.containingPackage.orEmpty()
        return when (annotation) {
            is KSAnnotationImpl -> processKtAnnotation(
                annotation.ktAnnotationEntry,
                annotation,
                packageName
            )
            is KSAnnotationJavaImpl -> processJavaAnnotation(
                annotation.psi,
                annotation,
                packageName
            )
            else -> {
                // One possible case is KSAnnotationDescriptorImpl, which happens when the annotation
                // is in source files instead of classpath (I think), but we don't need to handle
                // that since epoxy annotations are always on classpath.
                error("Unknown annotation implementation type ${annotation.javaClass}")
            }
        }
    }

    private fun processJavaAnnotation(
        psi: PsiAnnotation,
        annotation: KSAnnotationJavaImpl,
        packageName: String
    ): List<AnnotationWithReferenceValue> {
        return psi.parameterList
            .attributes
            .zip(annotation.arguments)
            .map { (psiNameValue, ksValueArgument) ->
                AnnotationWithReferenceValue(
                    name = ksValueArgument.name?.asString(),
                    value = ksValueArgument.value,
                    reference = extractJavaReferenceAnnotationArgument(
                        psiNameValue,
                        annotation,
                        packageName
                    )
                )
            }
    }

    private fun extractJavaReferenceAnnotationArgument(
        psiNameValue: PsiNameValuePair,
        annotation: KSAnnotationJavaImpl,
        packageName: String
    ): String? {
        // eg: R.layout.foo, com.example.R.layout.foo, layout.foo, etc
        return psiNameValue.value?.text?.let { annotationReference ->
            extractReferenceAnnotationArgument(annotationReference) { annotationReferencePrefix ->
                findMatchingImportPackageJava(
                    annotation.psi,
                    annotationReference,
                    annotationReferencePrefix,
                    packageName
                )
            }
        }
    }

    private fun extractReferenceAnnotationArgument(
        // eg: R.layout.foo, com.example.R.layout.foo, layout.foo, etc
        annotationReference: String,
        /**
         * Given the name referenced in source code, return the matching import for that name.
         */
        importLookup: (annotationReferencePrefix: String) -> ImportMatch,
    ): String? {
        // First part of dot reference, eg: "R"
        // If no dots, then it could be fully statically imported, so we take the full string.
        val annotationReferencePrefix =
            annotationReference.substringBefore(".").ifEmpty { return null }

        val importMatch = importLookup(annotationReferencePrefix)

        return importMatch.fullyQualifiedReference
    }

    private fun processKtAnnotation(
        annotationEntry: KtAnnotationEntry,
        annotation: KSAnnotation,
        packageName: String
    ): List<AnnotationWithReferenceValue> {
        return annotationEntry.valueArguments
            .zip(annotation.arguments)
            .flatMap { (valueArgument, ksValueArgument) ->

                val references = extractKotlinResourceReferencesInAnnotationArgument(
                    valueArgument,
                    annotationEntry,
                    packageName
                )

                val values = (ksValueArgument.value as? Iterable<*>)?.toList() ?: listOf(
                    ksValueArgument.value
                )

                if (values.size != references.size) {
                    error("Resource reference count does not match value count. Resources: $references values: $values annotation: $annotationEntry")
                }

                values.zip(references).map { (value, resourceReference) ->
                    AnnotationWithReferenceValue(
                        name = ksValueArgument.name?.asString(),
                        value = value,
                        reference = resourceReference
                    )
                }
            }
    }

    private fun extractKotlinResourceReferencesInAnnotationArgument(
        valueArgument: ValueArgument,
        annotationEntry: KtAnnotationEntry,
        packageName: String
    ): List<String> {
        return valueArgument.getArgumentExpression()?.let { ex ->

            val resourceNames = getResourceNamesFromAnnotationExpression(ex)

            resourceNames.mapNotNull { resourceName ->

                extractReferenceAnnotationArgument(resourceName) { annotationReferencePrefix ->
                    findMatchingImportPackageKt(
                        annotationEntry,
                        resourceName,
                        annotationReferencePrefix,
                        packageName
                    )
                }
            }
        } ?: emptyList()
    }

    private fun getResourceNamesFromAnnotationExpression(expression: KtExpression): List<String> {
        return if (expression is KtCollectionLiteralExpression) {
            // annotation argument is a array of resources
            expression.getInnerExpressions()
                .flatMap { getResourceNamesFromAnnotationExpression(expression) }
        } else {

            // eg: R.layout.foo, com.example.R.layout.foo, layout.foo, etc
            val annotationReference = fqNameFromExpression(expression)?.asString()

            listOfNotNull(annotationReference)
        }
    }

    private fun findMatchingImportPackageJava(
        annotationEntry: PsiAnnotation,
        annotationReference: String,
        annotationReferencePrefix: String,
        packageName: String
    ): ImportMatch {
        // Note: Star imports are not included in this, and there doesn't seem to be a way to resolve them, so
        // they are not included or supported.
        val importedNames = (annotationEntry.containingFile as? PsiJavaFile)
            ?.importList
            ?.importStatements
            ?.mapNotNull { it.qualifiedName }
            ?: emptyList()

        return findMatchingImportPackage(
            importedNames,
            annotationReference,
            annotationReferencePrefix,
            packageName
        )
    }

    private fun findMatchingImportPackageKt(
        annotationEntry: KtAnnotationEntry,
        annotationReference: String,
        annotationReferencePrefix: String,
        packageName: String
    ): ImportMatch {
        val importedNames = annotationEntry
            .containingKtFile
            .importDirectives
            .mapNotNull { it.importPath?.toString() }

        return findMatchingImportPackage(
            importedNames,
            annotationReference,
            annotationReferencePrefix,
            packageName
        )
    }

    sealed class ImportMatch {
        abstract val fullyQualifiedReference: String

        class TypeAlias(val import: String, alias: String, annotationReference: String) :
            ImportMatch() {
            // Example: Type alias "com.airbnb.paris.test.R2 as typeAliasedR"
            // import - com.airbnb.paris.test.R2
            // alias - typeAliasedR
            // annotationReference - typeAliasedR.layout.my_layout
            // actual fqn - com.airbnb.paris.test.R2.layout.my_layout
            override val fullyQualifiedReference: String =
                import.trim() + annotationReference.substringAfter(alias)
        }

        class Normal(val referenceImportPrefix: String, val annotationReference: String) :
            ImportMatch() {
            override val fullyQualifiedReference: String =
                referenceImportPrefix + (if (referenceImportPrefix.isNotEmpty()) "." else "") + annotationReference
        }
    }

    data class AnnotationWithReferenceValue(
        val name: String?,
        val value: Any?,
        val reference: String?
    ) {
        fun toResourceValue(): ResourceValue? {
            if (value !is Int || reference == null || reference.toIntOrNull() != null) return null

            val resourceInfo = when {
                ".R2." in reference || reference.startsWith("R2.") -> {
                    extractResourceInfo(reference, "R2")
                }
                ".R." in reference || reference.startsWith("R.") -> {
                    extractResourceInfo(reference, "R")
                }
                else -> {
                    error("Unsupported resource reference $reference")
                }
            }

            return ResourceValue(
                // Regardless of if the input is R or R2, we always need the generated code to reference R
                ClassName.get(resourceInfo.packageName, "R", resourceInfo.rSubclassName),
                resourceName = resourceInfo.resourceName,
                value,
            )
        }

        /**
         * @param reference fully qualified resource reference. eg com.example.R.layout.my_view
         * @param rClassSimpleName ie R or R2
         */
        private fun extractResourceInfo(
            reference: String,
            rClassSimpleName: String
        ): ResourceReferenceInfo {
            // get package before R and resource details after R
            val packageAndResourceType = reference.split(".$rClassSimpleName.").also {
                check(it.size == 2) { "Unexpected annotation value reference pattern $reference" }
            }

            val packageName = packageAndResourceType[0]

            val (rSubclass, resourceName) = packageAndResourceType[1].split(".").also {
                check(it.size == 2) { "Unexpected annotation value reference pattern $reference" }
            }

            return ResourceReferenceInfo(
                packageName = packageName,
                rSimpleName = rClassSimpleName,
                rSubclassName = rSubclass,
                resourceName = resourceName
            )
        }
    }

    private data class ResourceReferenceInfo(
        val packageName: String,
        val rSimpleName: String,
        val rSubclassName: String,
        val resourceName: String
    )

    // From https://github.com/JetBrains/kotlin/blob/92d200e093c693b3c06e53a39e0b0973b84c7ec5/compiler/psi/src/org/jetbrains/kotlin/psi/KtImportDirective.java
    private fun fqNameFromExpression(expression: KtExpression): FqName? {
        return when (expression) {
            is KtDotQualifiedExpression -> {
                val parentFqn: FqName? = fqNameFromExpression(expression.receiverExpression)
                val child: Name = expression.selectorExpression?.let { nameFromExpression(it) }
                    ?: return parentFqn
                parentFqn?.child(child)
            }
            is KtSimpleNameExpression -> {
                FqName.topLevel(expression.getReferencedNameAsName())
            }
            else -> {
                null
            }
        }
    }

    private fun nameFromExpression(expression: KtExpression): Name? {
        return if (expression is KtSimpleNameExpression) {
            expression.getReferencedNameAsName()
        } else {
            null
        }
    }

    companion object {
        internal fun findMatchingImportPackage(
            importedNames: List<String>,
            annotationReference: String,
            annotationReferencePrefix: String,
            packageName: String
        ): ImportMatch {
            // Match something like "com.airbnb.paris.test.R2 as typeAliasedR"
            val typeAliasRegex = Regex("(.*)\\s+as\\s+$annotationReferencePrefix\$")
            return importedNames.firstNotNullOfOrNull { importedName ->

                when {
                    importedName.endsWith(".$annotationReferencePrefix") -> {
                        // import com.example.R
                        // R.layout.my_layout -> R
                        Normal(
                            referenceImportPrefix = importedName.substringBeforeLast(".$annotationReferencePrefix"),
                            annotationReference = annotationReference
                        )
                    }
                    importedName.contains(typeAliasRegex) -> {
                        typeAliasRegex.find(importedName)?.groupValues?.getOrNull(1)
                            ?.let { import ->
                                TypeAlias(import, annotationReferencePrefix, annotationReference)
                            }
                    }
                    (!importedName.contains(".") && importedName == annotationReferencePrefix) -> {
                        // import foo
                        // foo.R.layout.my_layout -> foo
                        Normal("", annotationReference)
                    }
                    else -> null
                }
            } ?: run {
                // If first character in the reference is upper case, and we didn't find a matching import,
                // assume that it is a class reference in the same package (ie R class is in the same package, so we use the same package name)
                if (annotationReferencePrefix.firstOrNull()?.isUpperCase() == true) {
                    Normal(packageName, annotationReference)
                } else {
                    // Reference is already fully qualified so we don't need to prepend package info to the reference
                    Normal("", annotationReference)
                }
            }
        }
    }
}

/**
 * Easy way to retrieve the value of a field via reflection.
 *
 * @param fieldName Name of the field on this class
 * @param U The type of the field..
 */
inline fun <reified U> Any.getFieldWithReflection(fieldName: String): U {
    val value = try {
        val field = this.javaClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.get(this)
    } catch (e: NoSuchFieldException) {
        // Kotlin sometimes does not have a field backing a property, so we try a getter method
        // for it.
        val method = this.javaClass.getMethod("get${fieldName.capitalize()}")
        method.isAccessible = true
        method.invoke(this)
    }

    check(value is U) {
        "Expected field '$fieldName' to be ${U::class.java.simpleName} but got a ${value.javaClass.simpleName}"
    }
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    return value
}

inline fun <reified U> Any.getFieldWithReflectionOrNull(fieldName: String): U? {
    return kotlin.runCatching<U> {
        getFieldWithReflection(fieldName)
    }.getOrNull()
}
