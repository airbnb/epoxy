package com.airbnb.epoxy

import androidx.room.compiler.processing.util.Source
import androidx.room.compiler.processing.util.XTestInvocation
import androidx.room.compiler.processing.util.runProcessorTest
import com.airbnb.epoxy.processor.Logger
import com.airbnb.epoxy.processor.Memoizer
import com.airbnb.epoxy.processor.Utils
import com.airbnb.epoxy.processor.typeNameWithWorkaround
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue
import java.io.Serializable

class UtilsTests {

    @Test
    fun detectNestedClass() {
        val libSource = Source.kotlin(
            "lib.kt",
            """
            class KotlinClass {
                inner class MyInnerClass
                class MyNestedClass
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(libSource)) { invocation ->
            val innerType = invocation.processingEnv.requireTypeElement("KotlinClass.MyInnerClass")
            val nestedType =
                invocation.processingEnv.requireTypeElement("KotlinClass.MyNestedClass")

            expectThat(!innerType.isStatic())
            expectThat(nestedType.isStatic())
        }
    }

    @Test
    fun typeEnumCreation() {
        val libSource = Source.kotlin(
            "lib.kt",
            """
            class KotlinClass {
                inner class MyInnerClass
                class MyNestedClass
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(libSource)) { invocation ->
            val innerType = invocation.processingEnv.requireTypeElement("KotlinClass.MyInnerClass")
            val nestedType =
                invocation.processingEnv.requireTypeElement("KotlinClass.MyNestedClass")

            expectThat(!innerType.isStatic())
            expectThat(nestedType.isStatic())
        }
    }

    @Test
    fun testListVariance() {
        val libSource = Source.kotlin(
            "lib.kt",
            """
            class KotlinClass {
                fun foo(list: List<CharSequence>) {}
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(libSource)) { invocation ->
            val clazz = invocation.processingEnv.requireTypeElement("KotlinClass")
            val param = clazz.getDeclaredMethods().single().parameters.single()

            // Bug in KSP leads to invariance when it should be covariant?
            val memoizer = invocation.createMemoizer()
            if (invocation.isKsp) {
                expectThat(param.type.typeNameWithWorkaround(memoizer).toString())
                    .isEqualTo("java.util.List<? extends java.lang.CharSequence>")
            } else {
                expectThat(param.type.typeNameWithWorkaround(memoizer).toString())
                    .isEqualTo("java.util.List<? extends java.lang.CharSequence>")
            }
        }
    }

    @Test
    fun typeNameWorkaround() {
        val kotlinSource = Source.kotlin(
            "lib.kt",
            """
            class KotlinClass<out A, in B, C, D :  CharSequence, out E : List<*>, in F : List<*>> {
                fun <T> foo(
                    list: List<CharSequence>,
                    func3: (Integer, List<CharSequence>, String) -> Unit,
                    stringList: List<String>,
                    funcUnit: (Unit) -> Integer,
                    funcAny: (Any?) -> Integer,
                    keyedListener: com.airbnb.epoxy.KeyedListener<*, Map<*, *>>,
                    dataList: List<com.airbnb.epoxy.MyData>,
                    kotlinClass: KotlinClass<*, *, C, D, *, *>,
                    linkedHashSet: LinkedHashSet<com.airbnb.epoxy.DataPoint<Pair<Any, String>, Double>>,
                    set: Set<com.airbnb.epoxy.DataPoint<Pair<Any, String>, Double>>,
                    b: B,
                    bList: List<B>,
                    c: C,
                    d: D,
                    dList: List<D>,
                    f: F,
                    fList: List<F>,
                    t: T,
                    listT: List<T>,
                ){}
            }
            """.trimIndent()
        )
        val javaSource = Source.java(
            "foo.JavaClass",
            """
            package foo;
            import java.util.List;
            import java.util.Map;
            import java.lang.Integer;
            
            class JavaClass<T extends List<?>> {
                <T> void foo(
                    List<CharSequence> j1,
                    List<? extends CharSequence> j2,
                    List<String> j3,
                    com.airbnb.epoxy.KeyedListener<?, ? extends Map<Integer, ?>> j4,
                    List<com.airbnb.epoxy.MyData> j5,
                    T j6
                ){}
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(kotlinSource, javaSource)) { invocation ->

            val kotlinClass = invocation.processingEnv.requireTypeElement("KotlinClass")
            val javaClass = invocation.processingEnv.requireTypeElement("foo.JavaClass")

            val javaParams = javaClass.getDeclaredMethods().single().parameters
            val kotlinParams = kotlinClass.getDeclaredMethods().single().parameters

            val memoizer = invocation.createMemoizer()
            val params = javaParams.plus(kotlinParams).associate {
                it.name to lazy { it.type.typeNameWithWorkaround(memoizer).toString() }
            }

            infix fun String.named(expectedName: String) {
                expectThat(this) {
                    assert("in ${if (invocation.isKsp) "ksp" else "javac"} expected $expectedName") {
                        when (val value = params[this@named]?.value) {
                            null -> fail("but got null")
                            expectedName -> {
                                pass()
                            }
                            else -> {
                                fail("but got $value")
                            }
                        }
                    }
                }
            }

            // TODO: KSP sees "java.util.Set<com.airbnb.epoxy.DataPoint<kotlin.Pair<?, java.lang.String>, java.lang.Double>>" which seems like it might be correct, but is still different from javac
//            "set" named "java.util.Set<com.airbnb.epoxy.DataPoint<kotlin.Pair<java.lang.Object, java.lang.String>, java.lang.Double>>"

            // TODO: KSP sees only LinkedHashSet<E>
//            "linkedHashSet" named "java.util.LinkedHashSet<com.airbnb.epoxy.DataPoint<kotlin.Pair<java.lang.Object, java.lang.String>, java.lang.Double>>"

            "j1" named "java.util.List<java.lang.CharSequence>"
            "j2" named "java.util.List<? extends java.lang.CharSequence>"
            "j3" named "java.util.List<java.lang.String>"
            "j4" named "com.airbnb.epoxy.KeyedListener<?, ? extends java.util.Map<java.lang.Integer, ?>>"
            "j5" named "java.util.List<com.airbnb.epoxy.MyData>"
            "j6" named "T"
            "list" named "java.util.List<? extends java.lang.CharSequence>"
            "func3" named "kotlin.jvm.functions.Function3<? super java.lang.Integer, ? super java.util.List<? extends java.lang.CharSequence>, ? super java.lang.String, kotlin.Unit>"
            "stringList" named "java.util.List<java.lang.String>"
            "funcUnit" named "kotlin.jvm.functions.Function1<? super kotlin.Unit, java.lang.Integer>"
            "funcAny" named "kotlin.jvm.functions.Function1<java.lang.Object, java.lang.Integer>"
            "keyedListener" named "com.airbnb.epoxy.KeyedListener<?, java.util.Map<?, ?>>"
            "dataList" named "java.util.List<com.airbnb.epoxy.MyData>"
            // TODO: STAR wildcard case in KSP is not differentiable from type param name
//            "kotlinClass" named "KotlinClass<?, ?, C, D, ?, ?>"
            // TODO: ksp expected B : but got java.lang.Object
//            "b" named "B"
//            "bList" named "java.util.List<? extends B>"
//            "c" named "C"
//            "d" named "D"
//            "dList" named "java.util.List<? extends D>"
//            "f" named "F"
//            "fList" named "java.util.List<? extends F>"
//            "t" named "T"
//            "listT" named "java.util.List<? extends T>"
        }
    }

    @Test
    fun testWildcardInPoetName() {
        val libSource = Source.kotlin(
            "lib.kt",
            """
            class MyClass {
                fun setList(list: List<*>) {}
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(libSource)) { invocation ->
            val clazz = invocation.processingEnv.requireTypeElement("MyClass")
            val param = clazz.getDeclaredMethods().single().parameters.single()

            val memoizer = invocation.createMemoizer()
            expectThat(
                param.type.typeNameWithWorkaround(memoizer).toString()
            ).isEqualTo("java.util.List<?>")
            println(
                "${if (invocation.isKsp) "ksp" else "javac"} : " + param.type.typeNameWithWorkaround(
                    memoizer
                )
            )
            // Prints:
            // javac : java.util.List<?>
            // ksp : java.util.List<E>
        }
    }

    @Test
    fun checkMapType() {
        val libSource = Source.kotlin(
            "lib.kt",
            """
            class MyClass {
                val map: Map<Int, Int> = emptyMap()
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(libSource)) { invocation ->
            val clazz = invocation.processingEnv.requireTypeElement("MyClass")

            val field = clazz.getDeclaredFields().single()
            expectThat(Utils.isMapType(field.type)).isTrue()
        }

        val javaSource = Source.java(
            "foo.MyClass",
            """
            package foo;
            import java.util.Map;
            
            class MyClass {
                Map<Integer, Integer> map;
            }
            """.trimIndent()
        )
        runProcessorTest(listOf(javaSource)) { invocation ->
            val clazz = invocation.processingEnv.requireTypeElement("foo.MyClass")

            val field = clazz.getDeclaredFields().single()
            expectThat(Utils.isMapType(field.type)).isTrue()
        }
    }
}

class Foo<out A, in B, C, D : CharSequence, out E : List<*>, in F : List<*>> {
    fun foo(dataSet: LinkedHashSet<DataPoint<Pair<Any, String>, Double>>) {
        bar(dataSet)
    }

    fun bar(data: Set<DataPoint<Pair<Any, String>, Double>>) {
    }
}

fun XTestInvocation.createMemoizer() =
    Memoizer(processingEnv, Logger(processingEnv.messager, false))

data class KeyedListener<Key, Listener>(val identifier: Key, val listener: Listener)

data class MyData(val int: Int)

data class DataPoint<X : Serializable, Y>(
    val x: X,
    val y: Y?
) where Y : Number, Y : Comparable<Y>
