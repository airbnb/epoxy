package com.airbnb.epoxy

import com.airbnb.epoxy.ProcessorTestUtils.assertGeneration
import com.airbnb.epoxy.ProcessorTestUtils.assertGenerationError
import com.airbnb.epoxy.ProcessorTestUtils.processors
import com.google.common.truth.Truth.assert_
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory.javaSources
import org.junit.Test
import javax.tools.JavaFileObject

class ViewProcessorTest {

    @Test
    fun stringOverloads() {
        assertGeneration("TestStringOverloadsView.java", "TestStringOverloadsViewModel_.java")
    }

    @Test
    fun stringOverloads_throwsIfNotCharSequence() {
        assertGenerationError(
            "StringOverloads_throwsIfNotCharSequence.java",
            "must be a CharSequence"
        )
    }

    @Test
    fun nullStringOverloads() {
        assertGeneration(
            "TestNullStringOverloadsView.java",
            "TestNullStringOverloadsViewModel_.java"
        )
    }

    @Test
    fun manyTypes() {
        assertGeneration("TestManyTypesView.java", "TestManyTypesViewModel_.java")
    }

    @Test
    fun prop_throwsIfPrivate() {
        assertGenerationError("Prop_throwsIfPrivate.java", "private")
    }

    @Test
    fun prop_throwsIfStatic() {
        assertGenerationError("Prop_throwsIfStatic.java", "static")
    }

    @Test
    fun prop_throwsIfNoParams() {
        assertGenerationError("Prop_throwsIfNoParams.java", "must have exactly 1 parameter")
    }

    @Test
    fun prop_throwsIfMultipleParams() {
        assertGenerationError("Prop_throwsIfMultipleParams.java", "must have exactly 1 parameter")
    }

    @Test
    fun groups() {
        assertGeneration("PropGroupsView.java", "PropGroupsViewModel_.java")
    }

    @Test
    fun defaults() {
        assertGeneration("PropDefaultsView.java", "PropDefaultsViewModel_.java")
    }

    @Test
    fun defaults_throwsForNonStaticValue() {
        assertGenerationError("PropDefaultsView_throwsForNonStaticValue.java", "static")
    }

    @Test
    fun defaults_throwsForNonFinalValue() {
        assertGenerationError("PropDefaultsView_throwsForNonFinalValue.java", "final")
    }

    @Test
    fun defaults_throwsForPrivateValue() {
        assertGenerationError("PropDefaultsView_throwsForPrivateValue.java", "final")
    }

    @Test
    fun defaults_throwsForWrongType() {
        assertGenerationError("PropDefaultsView_throwsForWrongType.java", "must be a int")
    }

    @Test
    fun defaults_throwsForNotFound() {
        assertGenerationError("PropDefaultsView_throwsForNotFound.java", "could not be found")
    }

    @Test
    fun onViewRecycled() {
        assertGeneration("OnViewRecycledView.java", "OnViewRecycledViewModel_.java")
    }

    @Test
    fun onViewRecycled_throwsIfPrivate() {
        assertGenerationError("OnViewRecycledView_throwsIfPrivate.java", "private")
    }

    @Test
    fun onViewRecycled_throwsIfStatic() {
        assertGenerationError("OnViewRecycledView_throwsIfStatic.java", "static")
    }

    @Test
    fun onViewRecycled_throwsIfHasParams() {
        assertGenerationError(
            "OnViewRecycledView_throwsIfHasParams.java",
            "must have exactly 0 parameter"
        )
    }

    @Test
    fun onVisibilityChanged() {
        assertGeneration(
            "OnVisibilityChangedView.java",
            "OnVisibilityChangedViewModel_.java"
        )
    }

    @Test
    fun onVisibilityChanged_throwsIfPrivate() {
        assertGenerationError(
            "OnVisibilityChangedView_throwsIfPrivate.java",
            "private"
        )
    }

    @Test
    fun onVisibilityChanged_throwsIfStatic() {
        assertGenerationError(
            "OnVisibilityChangedView_throwsIfStatic.java",
            "static"
        )
    }

    @Test
    fun onVisibilityChanged_throwsIfInvalidParams() {
        assertGenerationError(
            "OnVisibilityChangedView_throwsIfInvalidParams.java",
            "must have parameter types [FLOAT, FLOAT, INT, INT], " +
                "found: [BOOLEAN, BOOLEAN, INT, INT] (method: onVisibilityChanged)"
        )
    }

    @Test
    fun onVisibilityChanged_throwsIfNoParams() {
        assertGenerationError(
            "OnVisibilityChangedView_throwsIfNoParams.java",
            "must have exactly 4 parameter (method: onVisibilityChanged)"
        )
    }

    @Test
    fun onVisibilityStateChanged() {
        assertGeneration(
            "OnVisibilityStateChangedView.java",
            "OnVisibilityStateChangedViewModel_.java"
        )
    }

    @Test
    fun onVisibilityStateChanged_throwsIfPrivate() {
        assertGenerationError(
            "OnVisibilityStateChangedView_throwsIfPrivate.java",
            "private"
        )
    }

    @Test
    fun onVisibilityStateChanged_throwsIfStatic() {
        assertGenerationError(
            "OnVisibilityStateChangedView_throwsIfStatic.java",
            "static"
        )
    }

    @Test
    fun onVisibilityStateChanged_throwsIfInvalidParams() {
        assertGenerationError(
            "OnVisibilityStateChangedView_throwsIfInvalidParams.java",
            "must have parameter types [INT], found: [BOOLEAN] (method: onVisibilityStateChanged)"
        )
    }

    @Test
    fun onVisibilityStateChanged_throwsIfNoParams() {
        assertGenerationError(
            "OnVisibilityStateChangedView_throwsIfNoParams.java",
            "must have exactly 1 parameter (method: onVisibilityStateChanged)"
        )
    }

    @Test
    fun nullOnRecycle() {
        assertGeneration("NullOnRecycleView.java", "NullOnRecycleViewModel_.java")
    }

    @Test
    fun nullOnRecycle_throwsIfNotNullable() {
        assertGenerationError("NullOnRecycleView_throwsIfNotNullable.java", "@Nullable")
    }

    @Test
    fun doNotHash() {
        assertGeneration("DoNotHashView.java", "DoNotHashViewModel_.java")
    }

    @Test
    fun objectWithoutEqualsThrows() {
        assertGenerationError(
            "ObjectWithoutEqualsThrowsView.java",
            "Attribute does not implement hashCode"
        )
    }

    @Test
    fun ignoreRequireHashCode() {
        assertGeneration("IgnoreRequireHashCodeView.java", "IgnoreRequireHashCodeViewModel_.java")
    }

    @Test
    fun savedState() {
        assertGeneration("SavedStateView.java", "SavedStateViewModel_.java")
    }

    @Test
    fun gridSpanCount() {
        assertGeneration("GridSpanCountView.java", "GridSpanCountViewModel_.java")
    }

    @Test
    fun baseModel() {
        val model = JavaFileObjects
            .forResource("BaseModelView.java".patchResource())

        val baseModel = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.TestBaseModel",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.widget.FrameLayout;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "\n" +
                    "public abstract class TestBaseModel<T extends FrameLayout> " +
                    "extends EpoxyModel<T> {\n" +
                    " public TestBaseModel(long id) { super(id); }" +
                    "\n" +
                    "  @Override\n" +
                    "  public void bind(T view) {\n" +
                    "    super.bind(view);\n" +
                    "  }\n" +
                    "\n" +
                    "  @Override\n" +
                    "  public void bind(T view, List<Object> payloads) {\n" +
                    "    super.bind(view, payloads);\n" +
                    "  }\n" +
                    "}\n"
            )

        val generatedModel = JavaFileObjects.forResource("BaseModelViewModel_.java".patchResource())

        assert_().about(javaSources())
            .that(listOf(baseModel, model))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun baseModelWithDiffBind() {
        val baseModel = JavaFileObjects.forSourceLines(
            "com.airbnb.epoxy.TestBaseModel",
            "package com.airbnb.epoxy;\n" +
                "\n" +
                "import android.widget.FrameLayout;\n" +
                "\n" +
                "public abstract class TestBaseModel<T extends FrameLayout> " +
                "extends EpoxyModel<T> {\n" +
                "@Override\n" +
                "  public void bind(T view, EpoxyModel<?> previouslyBoundModel) {\n" +
                "    super.bind(view, previouslyBoundModel);\n" +
                "  }" +
                "}"
        )

        assertGeneration(
            sourceFileNames = listOf("BaseModelView.java"),
            sourceObjects = listOf(baseModel),
            generatedFileNames = listOf("BaseModelViewWithSuperDiffBindModel_.java")
        )
    }

    @Test
    fun baseModelWithAttribute() {
        val model = JavaFileObjects
            .forResource("BaseModelView.java".patchResource())

        val baseModel = JavaFileObjects.forSourceLines(
            "com.airbnb.epoxy.TestBaseModel",
            "package com.airbnb.epoxy;\n" +
                "\n" +
                "import android.widget.FrameLayout;\n" +
                "\n" +
                "public abstract class TestBaseModel<T extends FrameLayout> " +
                "extends EpoxyModel<T> {\n" +
                "  @EpoxyAttribute String baseModelString;\n" +
                "}\n"
        )

        val generatedModel =
            JavaFileObjects.forResource("BaseModelWithAttributeViewModel_.java".patchResource())

        assert_().about(javaSources())
            .that(listOf(baseModel, model))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun throwsIfBaseModelNotEpoxyModel() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.BaseModelView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.widget.FrameLayout;\n" +
                    "\n" +
                    "@ModelView(defaultLayout = 1, baseModelClass = TestBaseModel.class)\n" +
                    "public class BaseModelView extends FrameLayout {\n" +
                    "\n" +
                    "  public BaseModelView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "  @ModelProp\n" +
                    "  public void setClickListener(String title) {\n" +
                    "\n" +
                    "  }\n" +
                    "}"
            )

        val baseModel = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.TestBaseModel",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "public abstract class TestBaseModel{\n" +
                    "}\n"
            )

        assert_().about(javaSources())
            .that(listOf(baseModel, model))
            .processedWith(processors())
            .failsToCompile()
            .withErrorContaining(
                "The base model provided to an ModelView must extend EpoxyModel"
            )
    }

    @Test
    fun baseModelFromPackageConfig() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.BaseModelView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.widget.FrameLayout;\n" +
                    "\n" +
                    "@ModelView(defaultLayout = 1)\n" +
                    "public class BaseModelView extends FrameLayout {\n" +
                    "\n" +
                    "  public BaseModelView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "  @ModelProp\n" +
                    "  public void setClickListener(String title) {\n" +
                    "\n" +
                    "  }\n" +
                    "}"
            )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R" +
                    ".class, defaultBaseModelClass = TestBaseModel.class)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R;\n" +
                    "import com.airbnb.epoxy.TestBaseModel;\n"
            )

        val baseModel = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.TestBaseModel",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "public abstract class TestBaseModel<T extends View> " +
                    "extends EpoxyModel<T> {\n" +
                    "}\n"
            )

        val generatedModel = JavaFileObjects.forResource(
            "BaseModelFromPackageConfigViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(baseModel, model, configClass, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun baseModelFromPackageConfigIsOverriddenByViewSetting() {
        // If a package default is set for the base model it can be overridden if the view sets its
        // own base model

        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.BaseModelView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "@ModelView(defaultLayout = 1, baseModelClass = EpoxyModel.class)\n" +
                    "public class BaseModelView extends View {\n" +
                    "\n" +
                    "  public BaseModelView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "  @ModelProp\n" +
                    "  public void setClickListener(String title) {\n" +
                    "\n" +
                    "  }\n" +
                    "}"
            )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R" +
                    ".class, defaultBaseModelClass = TestBaseModel.class)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R;\n" +
                    "import com.airbnb.epoxy.TestBaseModel;\n"
            )

        val baseModel = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.TestBaseModel",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "public abstract class TestBaseModel<T extends View> " +
                    "extends EpoxyModel<T> {\n" +
                    "}\n"
            )

        val generatedModel = JavaFileObjects.forResource(
            "BaseModelOverridesPackageConfigViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(baseModel, model, configClass, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun throwsIfBaseModelNotEpoxyModelInPackageConfig() {
        val model = JavaFileObjects
            .forResource("BaseModelView.java".patchResource())

        val baseModel = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.TestBaseModel",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "public abstract class TestBaseModel{\n" +
                    "}\n"
            )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R" +
                    ".class, defaultBaseModelClass = TestBaseModel.class)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R;\n" +
                    "import com.airbnb.epoxy.TestBaseModel;\n"
            )

        assert_().about(javaSources())
            .that(listOf(baseModel, model, configClass, R))
            .processedWith(processors())
            .failsToCompile()
            .withErrorContaining(
                "The base model provided to an ModelView must extend EpoxyModel"
            )
    }

    @Test
    fun rLayoutInViewModelAnnotationWorks() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.RLayoutInViewModelAnnotationWorksView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "@ModelView(defaultLayout = R.layout.res)\n" +
                    "public class RLayoutInViewModelAnnotationWorksView extends View {\n" +
                    "\n" +
                    "  public RLayoutInViewModelAnnotationWorksView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "}"
            )

        val generatedModel = JavaFileObjects.forResource(
            "RLayoutInViewModelAnnotationWorksViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(model, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun packageLayoutPatternDefault() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.DefaultPackageLayoutPatternView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "@ModelView\n" +
                    "public class DefaultPackageLayoutPatternView extends View {\n" +
                    "\n" +
                    "  public DefaultPackageLayoutPatternView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "}"
            )

        val R = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R {\n" +
                "  public static final class layout {\n" +
                "    public static final int default_package_layout_pattern_view = 0x7f040008;\n" +
                "  }\n" +
                "}"
        )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R" +
                    ".class)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R;\n"
            )

        val generatedModel = JavaFileObjects.forResource(
            "DefaultPackageLayoutPatternViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(model, configClass, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun packageLayoutPatternDefaultWithR2() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.DefaultPackageLayoutPatternView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "@ModelView\n" +
                    "public class DefaultPackageLayoutPatternView extends View {\n" +
                    "\n" +
                    "  public DefaultPackageLayoutPatternView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "}"
            )

        val R2 = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R2",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R2 {\n" +
                "  public static final class layout {\n" +
                "    public static final int default_package_layout_pattern_view = 0x7f040008;\n" +
                "  }\n" +
                "}"
        )

        val R = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R {\n" +
                "  public static final class layout {\n" +
                "    public static final int default_package_layout_pattern_view = 0x7f040008;\n" +
                "  }\n" +
                "}"
        )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R2" +
                    ".class)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R2;\n"
            )

        val generatedModel = JavaFileObjects.forResource(
            "DefaultPackageLayoutPatternViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(model, configClass, R2, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun packageLayoutCustomPattern() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.CustomPackageLayoutPatternView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "@ModelView\n" +
                    "public class CustomPackageLayoutPatternView extends View {\n" +
                    "\n" +
                    "  public CustomPackageLayoutPatternView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "}"
            )

        val R = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R {\n" +
                "  public static final class layout {\n" +
                "    public static final int hello_custom_package_layout_pattern_view_me = " +
                "0x7f040008;\n" +
                "  }\n" +
                "}"
        )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R" +
                    ".class, defaultLayoutPattern = \"hello_%s_me\")\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R;\n"
            )

        val generatedModel = JavaFileObjects.forResource(
            "CustomPackageLayoutPatternViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(model, configClass, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun layoutOverloads() {
        val model = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.LayoutOverloadsView",
                "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import android.content.Context;\n" +
                    "import android.view.View;\n" +
                    "\n" +
                    "@ModelView\n" +
                    "public class LayoutOverloadsView extends View {\n" +
                    "\n" +
                    "  public LayoutOverloadsView(Context context) {\n" +
                    "    super(context);\n" +
                    "  }\n" +
                    "\n" +
                    "}"
            )

        val R = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R {\n" +
                "  public static final class layout {\n" +
                "    public static final int layout_overloads_view = 0x7f040008;\n" +
                "    public static final int layout_overloads_view_one = 0x7f040009;\n" +
                "    public static final int layout_overloads_view_two = 0x7f04000a;\n" +
                "  }\n" +
                "}"
        )

        val configClass = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@PackageModelViewConfig(rClass = R" +
                    ".class, useLayoutOverloads = true)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                    "import com.airbnb.epoxy.R;\n"
            )

        val generatedModel = JavaFileObjects.forResource(
            "LayoutOverloadsViewModel_.java".patchResource()
        )

        assert_().about(javaSources())
            .that(listOf(model, configClass, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun generatedModelSuffix() {
        val model = JavaFileObjects.forSourceLines(
            "com.airbnb.epoxy.GeneratedModelSuffixView",
            "package com.airbnb.epoxy;\n" +
                "\n" +
                "import android.content.Context;\n" +
                "import android.view.View;\n" +
                "\n" +
                "@ModelView\n" +
                "public class GeneratedModelSuffixView extends View {\n" +
                "\n" +
                "  public GeneratedModelSuffixView(Context context) {\n" +
                "    super(context);\n" +
                "  }\n" +
                "\n" +
                "}"
        )

        val R = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R {\n" +
                "  public static final class layout {\n" +
                "    public static final int generated_model_suffix_view = 0x7f040008;\n" +
                "  }\n" +
                "}"
        )

        val configClass = JavaFileObjects.forSourceLines(
            "com.airbnb.epoxy.package-info",
            "@PackageModelViewConfig(rClass = R" +
                ".class, generatedModelSuffix = \"Suffix_\")\n" +
                "package com.airbnb.epoxy;\n" +
                "\n" +
                "import com.airbnb.epoxy.PackageModelViewConfig;\n" +
                "import com.airbnb.epoxy.R;\n"
        )

        assertGeneration(
            sourceObjects = listOf(model, configClass, R),
            generatedFileNames = listOf("GeneratedModelSuffixViewSuffix_.java")
        )
    }

    @Test
    fun afterBindProps() {
        val model = JavaFileObjects
            .forResource("TestAfterBindPropsView.java".patchResource())

        val superModel = JavaFileObjects
            .forResource("TestAfterBindPropsSuperView.java".patchResource())

        val generatedModel =
            JavaFileObjects.forResource("TestAfterBindPropsViewModel_.java".patchResource())

        assert_().about(javaSources())
            .that(listOf(model, superModel))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun textProp() {
        assertGeneration("TestTextPropView.java", "TestTextPropViewModel_.java")
    }

    @Test
    fun textPropMustBeCharSequence() {
        assertGenerationError("TestTextPropMustBeCharSequenceView.java", "must be a CharSequence")
    }

    @Test
    fun textPropDefault() {
        val model = JavaFileObjects
            .forResource("TextPropDefaultView.java".patchResource())

        val generatedModel =
            JavaFileObjects.forResource("TextPropDefaultViewModel_.java".patchResource())

        assert_().about(javaSources())
            .that(listOf(model, R))
            .processedWith(processors())
            .compilesWithoutError()
            .and()
            .generatesSources(generatedModel)
    }

    @Test
    fun textPropDefault_throwsForNonStringRes() {
        val model = JavaFileObjects
            .forResource("TextPropDefaultView_throwsForNonStringRes.java".patchResource())

        assert_().about(javaSources())
            .that(listOf(model, R))
            .processedWith(processors())
            .failsToCompile()
            .withErrorContaining("requires a string resource")
    }

    @Test
    fun callbackProp() {
        assertGeneration("TestCallbackPropView.java", "TestCallbackPropViewModel_.java")
    }

    @Test
    fun callbackPropMustBeNullable() {
        assertGenerationError("TestCallbackPropMustBeNullableView.java", "must be marked Nullable")
    }

    @Test
    fun testModelBuilderInterface() {
        assertGeneration("TestManyTypesView.java", "TestManyTypesViewModelBuilder.java")
    }

    @Test
    fun testAutoLayout() {
        assertGeneration("AutoLayoutModelView.java", "AutoLayoutModelViewModel_.java")
    }

    @Test
    fun testAutoLayoutMatchParent() {
        assertGeneration(
            "AutoLayoutModelViewMatchParent.java",
            "AutoLayoutModelViewMatchParentModel_.java"
        )
    }

    @Test
    fun testModelViewInheritsFromSuperClass() {
        assertViewsHaveModelsGenerated(
            "ModelViewSuperClass.java",
            "ModelViewExtendingSuperClass.java"
        )
    }

    @Test
    fun testFieldPropModelProp() {
        assertGeneration("TestFieldPropModelPropView.java", "TestFieldPropModelPropViewModel_.java")
    }

    @Test
    fun testFieldPropTextProp() {
        assertGeneration("TestFieldPropTextPropView.java", "TestFieldPropTextPropViewModel_.java")
    }

    @Test
    fun testFieldPropCallbackProp() {
        assertGeneration(
            "TestFieldPropCallbackPropView.java",
            "TestFieldPropCallbackPropViewModel_.java"
        )
    }

    @Test
    fun testFieldPropDoNotHashOption() {
        assertGeneration(
            "TestFieldPropDoNotHashOptionView.java",
            "TestFieldPropDoNotHashOptionViewModel_.java"
        )
    }

    @Test
    fun testFieldPropGenerateStringOverloadsOption() {
        assertGeneration(
            "TestFieldPropGenerateStringOverloadsOptionView.java",
            "TestFieldPropGenerateStringOverloadsOptionViewModel_.java"
        )
    }

    @Test
    fun testFieldPropNullOnRecycleOption() {
        assertGeneration(
            "TestFieldPropNullOnRecycleOptionView.java",
            "TestFieldPropNullOnRecycleOptionViewModel_.java"
        )
    }

    @Test
    fun testFieldPropIgnoreRequireHashCodeOption() {
        assertGeneration(
            "TestFieldPropIgnoreRequireHashCodeOptionView.java",
            "TestFieldPropIgnoreRequireHashCodeOptionViewModel_.java"
        )
    }

    @Test
    fun testFieldPropInheritFromParentView() {
        assertGeneration(
            listOf("TestFieldPropChildView.java", "TestFieldPropParentView.java"),
            listOf("TestFieldPropChildViewModel_.java")
        )
    }

    @Test
    fun testFieldPropThrowsIfPrivate() {
        assertGenerationError("TestFieldPropThrowsIfPrivateView.java", "private")
    }

    @Test
    fun testFieldPropThrowsIfStatic() {
        assertGenerationError("TestFieldPropThrowsIfStaticView.java", "static")
    }

    @Test
    fun testFieldPropStringOverloadsIfNotCharSequence() {
        assertGenerationError(
            "TestFieldPropStringOverloadsIfNotCharSequenceView.java",
            "must be a CharSequence"
        )
    }

    @Test
    fun testFieldPropTextPropIfNotCharSequence() {
        assertGenerationError("TestTextPropIfNotCharSequenceView.java", "must be a CharSequence")
    }

    @Test
    fun testStyleableView() {
        val configClass: JavaFileObject = JavaFileObjects
            .forSourceLines(
                "com.airbnb.epoxy.package-info",
                "@ParisConfig(rClass = R.class)\n" +
                    "package com.airbnb.epoxy;\n" +
                    "\n" +
                    "import com.airbnb.paris.annotations.ParisConfig;\n" +
                    "import com.airbnb.epoxy.R;\n"
            )

        assertGeneration(
            inputFile = "ModelViewWithParis.java",
            generatedFile = "ModelViewWithParisModel_.java",
            useParis = true,
            helperObjects = listOf(configClass, R)
        )
    }

    private fun assertViewsHaveModelsGenerated(vararg viewFiles: String) {
        assertGeneration(viewFiles.toList(), viewFiles.map { it.replace(".", "Model_.") })
    }

    companion object {

        private val R: JavaFileObject = JavaFileObjects.forSourceString(
            "com.airbnb.epoxy.R",
            "" +
                "package com.airbnb.epoxy;\n" +
                "public final class R {\n" +
                "  public static final class layout {\n" +
                "    public static final int res = 0x7f040008;\n" +
                "  }\n" +
                "  public static final class string {\n" +
                "    public static final int string_resource_value = 0x7f040009;\n" +
                "  }\n" +
                "}"
        )
    }
}
