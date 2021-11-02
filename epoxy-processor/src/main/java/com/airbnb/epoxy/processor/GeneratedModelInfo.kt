package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.XVariableElement
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.processor.Utils.buildEpoxyException
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import java.util.ArrayList
import java.util.LinkedHashSet
import javax.lang.model.element.Modifier

abstract class GeneratedModelInfo(val memoizer: Memoizer) {

    lateinit var superClassElement: XTypeElement
    lateinit var superClassName: TypeName
    lateinit var parameterizedGeneratedName: TypeName
    lateinit var generatedName: ClassName

    /**
     * Get the object type this model is typed with.
     */
    var modelType: TypeName? = null
        protected set
    var shouldGenerateModel = false

    /**
     * If true, any layout classes that exist that are prefixed by the default layout are included in
     * the generated model as other layout options via a generated method for each alternate layout.
     */
    var includeOtherLayoutOptions = false

    val attributeInfo: MutableList<AttributeInfo> = mutableListOf()

    @get:Synchronized
    val attributeInfoImmutable: List<AttributeInfo>
        get() = attributeInfo.toList()

    val typeVariableNames: MutableList<TypeVariableName> = ArrayList()
    val constructors: MutableList<ConstructorInfo> = ArrayList()
    val methodsReturningClassType: MutableSet<MethodInfo> = LinkedHashSet()
    val attributeGroups: MutableList<AttributeGroup> = ArrayList()
    private val attributeToGroup = mutableMapOf<AttributeInfo, AttributeGroup>()
    val annotations: MutableList<AnnotationSpec> = ArrayList()

    /**
     * The info for the style builder if this is a model view annotated with @Styleable. Null
     * otherwise.
     */
    var styleBuilderInfo: ParisStyleAttributeInfo? = null
        private set

    /**
     * An option set via [com.airbnb.epoxy.ModelView.autoLayout] to have Epoxy create the
     * view programmatically
     * instead of via xml layout resource inflation.
     */
    var layoutParams = ModelView.Size.NONE

    /**
     * The elements that influence the generation of this model.
     * eg base model class for @EpoxyModelClass, view class for @ModelView, etc
     */
    fun originatingElements(): List<XElement> {
        return listOfNotNull(styleBuilderInfo?.styleBuilderElement)
            .plus(additionalOriginatingElements())
    }

    open fun additionalOriginatingElements(): List<XElement> = emptyList()

    /**
     * Get information about constructors of the original class so we can duplicate them in the
     * generated class and call through to super with the proper parameters
     */
    fun getClassConstructors(classElement: XTypeElement): List<ConstructorInfo> {
        return memoizer.getClassConstructors(classElement, memoizer)
    }

    /**
     * Get information about methods returning class type of the original class so we can duplicate
     * them in the generated class for chaining purposes
     */
    fun collectMethodsReturningClassType(superModelClass: XTypeElement) {
        methodsReturningClassType
            .addAll(memoizer.getMethodsReturningClassType(superModelClass.type, memoizer))
    }

    @Synchronized
    fun addAttribute(attributeInfo: AttributeInfo) {
        addAttributes(listOf(attributeInfo))
    }

    @Synchronized
    fun addAttributes(attributesToAdd: Collection<AttributeInfo>) {
        removeMethodIfDuplicatedBySetter(attributesToAdd)

        // Overwrite duplicates while preserving ordering
        for (attribute in attributesToAdd) {
            val existingIndex = attributeInfo.indexOf(attribute)
            if (existingIndex > -1) {
                attributeInfo[existingIndex] = attribute
            } else {
                attributeInfo.add(attribute)
            }
        }
    }

    @Synchronized
    fun addAttributeIfNotExists(attributeToAdd: AttributeInfo) {
        if (attributeToAdd !in attributeInfo) {
            addAttribute(attributeToAdd)
        }
    }

    private fun removeMethodIfDuplicatedBySetter(attributeInfos: Collection<AttributeInfo>) {
        for (attributeInfo in attributeInfos) {
            val iterator = methodsReturningClassType.iterator()
            while (iterator.hasNext()) {
                val (name, _, params) = iterator.next()
                if (name == attributeInfo.fieldName && params.size == 1 && params[0].type == attributeInfo.typeName) {
                    iterator.remove()
                }
            }
        }
    }

    val typeVariables: Iterable<TypeVariableName>
        get() = typeVariableNames

    val isStyleable: Boolean
        get() = styleBuilderInfo != null

    fun setStyleable(
        parisStyleAttributeInfo: ParisStyleAttributeInfo
    ) {
        styleBuilderInfo = parisStyleAttributeInfo
        addAttribute(parisStyleAttributeInfo)
    }

    val isProgrammaticView: Boolean
        get() = isStyleable || layoutParams != ModelView.Size.NONE

    fun hasEmptyConstructor(): Boolean {
        return constructors.isEmpty() || constructors.any { it.params.isEmpty() }
    }

    /**
     * @return True if the super class of this generated model is also extended from a generated
     * model.
     */
    val isSuperClassAlsoGenerated: Boolean
        get() = superClassElement.type.isSubTypeOf(memoizer.generatedModelType)

    data class ConstructorInfo internal constructor(
        val modifiers: Set<Modifier>,
        val params: List<ParameterSpec>,
        val varargs: Boolean
    )

    override fun toString(): String {
        return (
            "GeneratedModelInfo{" +
                "attributeInfo=" + attributeInfo +
                ", superClassName=" + superClassName +
                '}'
            )
    }

    @Throws(EpoxyProcessorException::class)
    fun addAttributeGroup(
        groupName: String?,
        attributes: List<AttributeInfo>
    ) {
        var defaultAttribute: AttributeInfo? = null
        for (attribute in attributes) {
            if (attribute.isRequired ||
                attribute.codeToSetDefault.isEmpty && !hasDefaultKotlinValue(
                        attribute
                    )
            ) {
                continue
            }
            val hasSetExplicitDefault =
                defaultAttribute != null && hasExplicitDefault(defaultAttribute)

            // Have the first explicit default value in the group trump everything else.
            // If there are multiple set just ignore the rest. This simplifies our lookup
            // of kotlin default params since it's hard to know exactly which function has
            // set a default param (if they have the same function name and param name)
            if (hasSetExplicitDefault) {
                continue
            }

            // If only implicit
            // defaults exist, have a null default trump default primitives. This makes it so if there
            // is a nullable object and a primitive in a group, the default value will be to null out the
            // object.
            if (defaultAttribute == null || hasExplicitDefault(attribute) ||
                attribute.hasSetNullability()
            ) {
                defaultAttribute = attribute
            }
        }
        val group = AttributeGroup(groupName, attributes, defaultAttribute)
        attributeGroups.add(group)
        attributes.forEach {
            attributeToGroup[it] = group
        }
    }

    /**
     * If this attribute is in a group, returns the other attributes contained in that group.
     */
    fun otherAttributesInGroup(attribute: AttributeInfo): List<AttributeInfo> {
        return attributeToGroup[attribute]
            ?.attributes
            ?.minus(attribute)
            ?: emptyList()
    }

    fun isOverload(attribute: AttributeInfo): Boolean {
        return attributeToGroup[attribute]?.attributes?.let { it.size > 1 } == true
    }

    fun attributeGroup(attribute: AttributeInfo): AttributeGroup? {
        return attributeToGroup[attribute]
    }

    class AttributeGroup internal constructor(
        groupName: String?,
        attributes: List<AttributeInfo>,
        defaultAttribute: AttributeInfo?
    ) {
        val name: String?
        val attributes: List<AttributeInfo>
        val isRequired: Boolean
        val defaultAttribute: AttributeInfo?

        init {
            if (attributes.isEmpty()) {
                throw buildEpoxyException("Attributes cannot be empty")
            }
            if (defaultAttribute != null && defaultAttribute.codeToSetDefault.isEmpty &&
                !hasDefaultKotlinValue(defaultAttribute)
            ) {
                throw buildEpoxyException("Default attribute has no default code")
            }
            this.defaultAttribute = defaultAttribute
            isRequired = defaultAttribute == null
            name = groupName
            this.attributes =
                ArrayList(attributes)
        }
    }

    companion object {
        const val RESET_METHOD = "reset"
        const val GENERATED_CLASS_NAME_SUFFIX = "_"
        const val GENERATED_MODEL_SUFFIX = "Model$GENERATED_CLASS_NAME_SUFFIX"

        fun buildParamSpecs(params: List<XVariableElement>, memoizer: Memoizer): List<ParameterSpec> {
            return params.map { it.toParameterSpec(memoizer) }
        }

        private fun hasDefaultKotlinValue(attribute: AttributeInfo): Boolean {
            return (attribute as? ViewAttributeInfo)?.hasDefaultKotlinValue == true
        }

        private fun hasExplicitDefault(attribute: AttributeInfo): Boolean {
            if (attribute.codeToSetDefault.explicit != null) {
                return true
            }

            return (attribute as? ViewAttributeInfo)?.hasDefaultKotlinValue == true
        }
    }
}
