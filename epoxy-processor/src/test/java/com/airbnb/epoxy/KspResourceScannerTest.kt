package com.airbnb.epoxy

import androidx.room.compiler.processing.compat.XConverters.toJavac
import androidx.room.compiler.processing.util.XTestInvocation
import com.airbnb.epoxy.processor.resourcescanning.JavacResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.KspResourceScanner
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class KspResourceScannerTest {
    @Test
    fun findMatchingImportPackage_TypeAlias() {
        val import = KspResourceScanner.findMatchingImportPackage(
            importedNames = listOf("com.airbnb.paris.test.R2 as typeAliasedR"),
            annotationReference = "typeAliasedR.layout.my_layout",
            annotationReferencePrefix = "typeAliasedR",
            packageName = "com.airbnb.paris"
        )

        expectThat(import.fullyQualifiedReference).isEqualTo("com.airbnb.paris.test.R2.layout.my_layout")
    }

    @Test
    fun findMatchingImportPackage_TypeAliasDoesNotMatch() {
        val import = KspResourceScanner.findMatchingImportPackage(
            importedNames = listOf("com.airbnb.paris.test.R2 as typeAliasedR2"),
            annotationReference = "typeAliasedR.layout.my_layout",
            annotationReferencePrefix = "typeAliasedR",
            packageName = "com.airbnb.paris"
        )

        // falls back to annotation reference, since import should not match
        expectThat(import.fullyQualifiedReference).isEqualTo("typeAliasedR.layout.my_layout")
    }

    @Test
    fun findMatchingImportPackage_fullyStaticImport() {
        val import = KspResourceScanner.findMatchingImportPackage(
            importedNames = listOf("com.airbnb.n2.comp.designsystem.hostdls.R2.styleable.n2_CarouselCheckedActionCard_n2_layoutStyle"),
            annotationReference = "n2_CarouselCheckedActionCard_n2_layoutStyle",
            annotationReferencePrefix = "n2_CarouselCheckedActionCard_n2_layoutStyle",
            packageName = "com.airbnb.n2.comp.designsystem.hostdls"
        )

        // falls back to annotation reference, since import should not match
        expectThat(import.fullyQualifiedReference).isEqualTo("com.airbnb.n2.comp.designsystem.hostdls.R2.styleable.n2_CarouselCheckedActionCard_n2_layoutStyle")
    }

//    @Test
//    fun extractResourceValueListFromKotlinSource() {
//        runProcessorTest() { invocation ->
//            val type = invocation.processingEnv.requireTypeElement("com.airbnb.epoxy.MyTestKotlinObjectWithResourceArray")
//
//            val scanner = createResourceScanner(invocation)
//
//            val resourceValues =
//                scanner.getResourceValueList(EpoxyDataBindingLayouts::class, type, "value")
//
//            // TODO
//            println(resourceValues)
//        }
//    }
//
//    // TODO: Java source version
//
//    @Test
//    fun extractResourceValueFromKotlinSource() {
//        runProcessorTest() { invocation ->
//            val type = invocation.processingEnv.requireTypeElement("com.airbnb.epoxy.MyTestKotlinObjectWithSingleResource")
//
//            val scanner = createResourceScanner(invocation)
//
//            val resourceValue =
//                scanner.getResourceValue(EpoxyModelClass::class, type, "layout")
//
//            println(resourceValue?.code)
//        }
//    }

    private fun createResourceScanner(invocation: XTestInvocation): ResourceScanner {
        val scanner = if (invocation.isKsp) {
            KspResourceScanner({ invocation.processingEnv })
        } else {
            JavacResourceScanner(
                invocation.processingEnv.toJavac(),
                { invocation.processingEnv }
            )
        }
        return scanner
    }
}

@EpoxyDataBindingLayouts(value = [R.Layout.mylayout, R.Layout.mylayout2])
private object MyTestKotlinObjectWithResourceArray

@EpoxyModelClass(layout = R.Layout.mylayout)
private object MyTestKotlinObjectWithSingleResource

object R {
    object Layout {
        const val mylayout = 1
        const val mylayout2 = 3
    }
}
