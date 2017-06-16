package com.airbnb.epoxy;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ViewProcessorTest {

  private static final JavaFileObject R = JavaFileObjects.forSourceString("com.airbnb.epoxy.R", ""
      + "package com.airbnb.epoxy;\n"
      + "public final class R {\n"
      + "  public static final class layout {\n"
      + "    public static final int res = 0x7f040008;\n"
      + "  }\n"
      + "}"
  );

  private static final JavaFileObject R2 = JavaFileObjects.forSourceString("com.airbnb.epoxy.R2", ""
      + "package com.airbnb.epoxy;\n"
      + "public final class R2 {\n"
      + "  public static final class array {\n"
      + "    public static final int res = 0x7f040001;\n"
      + "  }\n"
      + "  public static final class bool {\n"
      + "    public static final int res = 0x7f040002;\n"
      + "  }\n"
      + "  public static final class color {\n"
      + "    public static final int res = 0x7f040003;\n"
      + "  }\n"
      + "  public static final class layout {\n"
      + "    public static final int res = 0x7f040008;\n"
      + "  }\n"
      + "  public static final class integer {\n"
      + "    public static final int res = 0x7f040004;\n"
      + "  }\n"
      + "  public static final class styleable {\n"
      + "    public static final int[] ActionBar = { 0x7f010001, 0x7f010003 };\n"
      + "  }\n"
      + "}"
  );

  @Test
  public void stringOverloads() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestStringOverloadsView.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("TestStringOverloadsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void stringOverloads_throwsIfNotCharSequence() {
    JavaFileObject model = JavaFileObjects
        .forResource("StringOverloads_throwsIfNotCharSequence.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must be a CharSequence");
  }

  @Test
  public void nullStringOverloads() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestNullStringOverloadsView.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("TestNullStringOverloadsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void manyTypes() {
    JavaFileObject model = JavaFileObjects
        .forResource("TestManyTypesView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("TestManyTypesViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void prop_throwsIfPrivate() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfPrivate.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private");
  }

  @Test
  public void prop_throwsIfStatic() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfStatic.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void prop_throwsIfNoParams() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfNoParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must have exactly 1 parameter");
  }

  @Test
  public void prop_throwsIfMultipleParams() {
    JavaFileObject model = JavaFileObjects
        .forResource("Prop_throwsIfMultipleParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must have exactly 1 parameter");
  }

  @Test
  public void groups() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropGroupsView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("PropGroupsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void defaults() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("PropDefaultsViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void defaults_throwsForNonStaticValue() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForNonStaticValue.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void defaults_throwsForNonFinalValue() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForNonFinalValue.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("final");
  }

  @Test
  public void defaults_throwsForPrivateValue() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForPrivateValue.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("final");
  }

  @Test
  public void defaults_throwsForWrongType() {
    JavaFileObject model = JavaFileObjects
        .forResource("PropDefaultsView_throwsForWrongType.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must be a int");
  }

  @Test
  public void onViewRecycled() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("OnViewRecycledViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void onViewRecycled_throwsIfPrivate() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView_throwsIfPrivate.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("private");
  }

  @Test
  public void onViewRecycled_throwsIfStatic() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView_throwsIfStatic.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("static");
  }

  @Test
  public void onViewRecycled_throwsIfHasParams() {
    JavaFileObject model = JavaFileObjects
        .forResource("OnViewRecycledView_throwsIfHasParams.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("must have exactly 0 parameter");
  }

  @Test
  public void nullOnRecycle() {
    JavaFileObject model = JavaFileObjects
        .forResource("NullOnRecycleView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("NullOnRecycleViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void nullOnRecycle_throwsIfNotNullable() {
    JavaFileObject model = JavaFileObjects
        .forResource("NullOnRecycleView_throwsIfNotNullable.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("@Nullable");
  }

  @Test
  public void doNotHash() {
    JavaFileObject model = JavaFileObjects
        .forResource("DoNotHashView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("DoNotHashViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void objectWithoutEqualsThrows() {
    JavaFileObject model = JavaFileObjects
        .forResource("ObjectWithoutEqualsThrowsView.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining("Attribute does not implement hashCode");
  }

  @Test
  public void ignoreRequireHashCode() {
    JavaFileObject model = JavaFileObjects
        .forResource("IgnoreRequireHashCodeView.java");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("IgnoreRequireHashCodeViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void savedState() {
    JavaFileObject model = JavaFileObjects
        .forResource("SavedStateView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("SavedStateViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void gridSpanCount() {
    JavaFileObject model = JavaFileObjects
        .forResource("GridSpanCountView.java");

    JavaFileObject generatedModel = JavaFileObjects.forResource("GridSpanCountViewModel_.java");

    assert_().about(javaSource())
        .that(model)
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void baseModel() {
    JavaFileObject model = JavaFileObjects
        .forResource("BaseModelView.java");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "import java.util.List;\n"
            + "\n"
            + "public abstract class TestBaseModel<T extends FrameLayout> extends EpoxyModel<T> {\n"
            + "\n"
            + "  @Override\n"
            + "  public void bind(T view) {\n"
            + "    super.bind(view);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public void bind(T view, List<Object> payloads) {\n"
            + "    super.bind(view, payloads);\n"
            + "  }\n"
            + "}\n");

    JavaFileObject generatedModel = JavaFileObjects.forResource("BaseModelViewModel_.java");

    assert_().about(javaSources())
        .that(asList(baseModel, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void baseModelWithDiffBind() {
    JavaFileObject model = JavaFileObjects
        .forResource("BaseModelView.java");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "public abstract class TestBaseModel<T extends FrameLayout> extends EpoxyModel<T> {\n"
            + "@Override\n"
            + "  public void bind(T view, EpoxyModel<?> previouslyBoundModel) {\n"
            + "    super.bind(view, previouslyBoundModel);\n"
            + "  }"
            + "}");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("BaseModelViewWithSuperDiffBindModel_.java");

    assert_().about(javaSources())
        .that(asList(baseModel, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void baseModelWithAttribute() {
    JavaFileObject model = JavaFileObjects
        .forResource("BaseModelView.java");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "public abstract class TestBaseModel<T extends FrameLayout> extends EpoxyModel<T> {\n"
            + "  @EpoxyAttribute String baseModelString;\n"
            + "}\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("BaseModelWithAttributeViewModel_.java");

    assert_().about(javaSources())
        .that(asList(baseModel, model))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void throwsIfBaseModelNotEpoxyModel() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.BaseModelView", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.content.Context;\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "@ModelView(defaultLayout = 1, baseModelClass = TestBaseModel.class)\n"
            + "public class BaseModelView extends FrameLayout {\n"
            + "\n"
            + "  public BaseModelView(Context context) {\n"
            + "    super(context);\n"
            + "  }\n"
            + "\n"
            + "  @ModelProp\n"
            + "  public void setClickListener(String title) {\n"
            + "\n"
            + "  }\n"
            + "}");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "public abstract class TestBaseModel{\n"
            + "}\n");

    assert_().about(javaSources())
        .that(asList(baseModel, model))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(
            "The base model provided to an ModelView must extend EpoxyModel");
  }

  @Test
  public void baseModelFromPackageConfig() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.BaseModelView", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.content.Context;\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "@ModelView(defaultLayout = 1)\n"
            + "public class BaseModelView extends FrameLayout {\n"
            + "\n"
            + "  public BaseModelView(Context context) {\n"
            + "    super(context);\n"
            + "  }\n"
            + "\n"
            + "  @ModelProp\n"
            + "  public void setClickListener(String title) {\n"
            + "\n"
            + "  }\n"
            + "}");

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R"
            + ".class, defaultBaseModelClass = TestBaseModel.class)\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R;\n"
            + "import com.airbnb.epoxy.TestBaseModel;\n");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "public abstract class TestBaseModel<T extends FrameLayout> extends EpoxyModel<T> {\n"
            + "}\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("BaseModelFromPackageConfigViewModel_.java");

    assert_().about(javaSources())
        .that(asList(baseModel, model, configClass, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void baseModelFromPackageConfigIsOverriddenByViewSetting() {
    // If a package default is set for the base model it can be overridden if the view sets its
    // own base model

    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.BaseModelView", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.content.Context;\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "@ModelView(defaultLayout = 1, baseModelClass = EpoxyModel.class)\n"
            + "public class BaseModelView extends FrameLayout {\n"
            + "\n"
            + "  public BaseModelView(Context context) {\n"
            + "    super(context);\n"
            + "  }\n"
            + "\n"
            + "  @ModelProp\n"
            + "  public void setClickListener(String title) {\n"
            + "\n"
            + "  }\n"
            + "}");

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R"
            + ".class, defaultBaseModelClass = TestBaseModel.class)\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R;\n"
            + "import com.airbnb.epoxy.TestBaseModel;\n");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "import android.widget.FrameLayout;\n"
            + "\n"
            + "public abstract class TestBaseModel<T extends FrameLayout> extends EpoxyModel<T> {\n"
            + "}\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("BaseModelOverridesPackageConfigViewModel_.java");

    assert_().about(javaSources())
        .that(asList(baseModel, model, configClass, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void throwsIfBaseModelNotEpoxyModelInPackageConfig() {
    JavaFileObject model = JavaFileObjects
        .forResource("BaseModelView.java");

    JavaFileObject baseModel = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.TestBaseModel", "package com.airbnb.epoxy;\n"
            + "\n"
            + "public abstract class TestBaseModel{\n"
            + "}\n");

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R"
            + ".class, defaultBaseModelClass = TestBaseModel.class)\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R;\n"
            + "import com.airbnb.epoxy.TestBaseModel;\n");

    assert_().about(javaSources())
        .that(asList(baseModel, model, configClass, R))
        .processedWith(new EpoxyProcessor())
        .failsToCompile()
        .withErrorContaining(
            "The base model provided to an ModelView must extend EpoxyModel");
  }

  @Test
  public void rLayoutInViewModelAnnotationWorks() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.RLayoutInViewModelAnnotationWorksView",
            "package com.airbnb.epoxy;\n"
                + "\n"
                + "import android.content.Context;\n"
                + "import android.view.View;\n"
                + "\n"
                + "@ModelView(defaultLayout = R.layout.res)\n"
                + "public class RLayoutInViewModelAnnotationWorksView extends View {\n"
                + "\n"
                + "  public RLayoutInViewModelAnnotationWorksView(Context context) {\n"
                + "    super(context);\n"
                + "  }\n"
                + "}");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("RLayoutInViewModelAnnotationWorksViewModel_.java");

    assert_().about(javaSources())
        .that(asList(model, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void packageLayoutPatternDefault() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.DefaultPackageLayoutPatternView",
            "package com.airbnb.epoxy;\n"
                + "\n"
                + "import android.content.Context;\n"
                + "import android.view.View;\n"
                + "\n"
                + "@ModelView\n"
                + "public class DefaultPackageLayoutPatternView extends View {\n"
                + "\n"
                + "  public DefaultPackageLayoutPatternView(Context context) {\n"
                + "    super(context);\n"
                + "  }\n"
                + "\n"
                + "}");

    JavaFileObject R = JavaFileObjects.forSourceString("com.airbnb.epoxy.R", ""
        + "package com.airbnb.epoxy;\n"
        + "public final class R {\n"
        + "  public static final class layout {\n"
        + "    public static final int default_package_layout_pattern_view = 0x7f040008;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R"
            + ".class)\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R;\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("DefaultPackageLayoutPatternViewModel_.java");

    assert_().about(javaSources())
        .that(asList(model, configClass, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void packageLayoutPatternDefaultWithR2() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.DefaultPackageLayoutPatternView",
            "package com.airbnb.epoxy;\n"
                + "\n"
                + "import android.content.Context;\n"
                + "import android.view.View;\n"
                + "\n"
                + "@ModelView\n"
                + "public class DefaultPackageLayoutPatternView extends View {\n"
                + "\n"
                + "  public DefaultPackageLayoutPatternView(Context context) {\n"
                + "    super(context);\n"
                + "  }\n"
                + "\n"
                + "}");

    JavaFileObject R2 = JavaFileObjects.forSourceString("com.airbnb.epoxy.R2", ""
        + "package com.airbnb.epoxy;\n"
        + "public final class R2 {\n"
        + "  public static final class layout {\n"
        + "    public static final int default_package_layout_pattern_view = 0x7f040008;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject R = JavaFileObjects.forSourceString("com.airbnb.epoxy.R", ""
        + "package com.airbnb.epoxy;\n"
        + "public final class R {\n"
        + "  public static final class layout {\n"
        + "    public static final int default_package_layout_pattern_view = 0x7f040008;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R2"
            + ".class)\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R2;\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("DefaultPackageLayoutPatternViewModel_.java");

    assert_().about(javaSources())
        .that(asList(model, configClass, R2, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void packageLayoutCustomPattern() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.CustomPackageLayoutPatternView",
            "package com.airbnb.epoxy;\n"
                + "\n"
                + "import android.content.Context;\n"
                + "import android.view.View;\n"
                + "\n"
                + "@ModelView\n"
                + "public class CustomPackageLayoutPatternView extends View {\n"
                + "\n"
                + "  public CustomPackageLayoutPatternView(Context context) {\n"
                + "    super(context);\n"
                + "  }\n"
                + "\n"
                + "}");

    JavaFileObject R = JavaFileObjects.forSourceString("com.airbnb.epoxy.R", ""
        + "package com.airbnb.epoxy;\n"
        + "public final class R {\n"
        + "  public static final class layout {\n"
        + "    public static final int hello_custom_package_layout_pattern_view_me = 0x7f040008;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R"
            + ".class, defaultLayoutPattern = \"hello_%s_me\")\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R;\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("CustomPackageLayoutPatternViewModel_.java");

    assert_().about(javaSources())
        .that(asList(model, configClass, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }

  @Test
  public void layoutOverloads() {
    JavaFileObject model = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.LayoutOverloadsView",
            "package com.airbnb.epoxy;\n"
                + "\n"
                + "import android.content.Context;\n"
                + "import android.view.View;\n"
                + "\n"
                + "@ModelView\n"
                + "public class LayoutOverloadsView extends View {\n"
                + "\n"
                + "  public LayoutOverloadsView(Context context) {\n"
                + "    super(context);\n"
                + "  }\n"
                + "\n"
                + "}");

    JavaFileObject R = JavaFileObjects.forSourceString("com.airbnb.epoxy.R", ""
        + "package com.airbnb.epoxy;\n"
        + "public final class R {\n"
        + "  public static final class layout {\n"
        + "    public static final int layout_overloads_view = 0x7f040008;\n"
        + "    public static final int layout_overloads_view_one = 0x7f040009;\n"
        + "    public static final int layout_overloads_view_two = 0x7f04000a;\n"
        + "  }\n"
        + "}"
    );

    JavaFileObject configClass = JavaFileObjects
        .forSourceLines("com.airbnb.epoxy.package-info", "@PackageModelViewConfig(rClass = R"
            + ".class, useLayoutOverloads = true)\n"
            + "package com.airbnb.epoxy;\n"
            + "\n"
            + "import com.airbnb.epoxy.PackageModelViewConfig;\n"
            + "import com.airbnb.epoxy.R;\n");

    JavaFileObject generatedModel =
        JavaFileObjects.forResource("LayoutOverloadsViewModel_.java");

    assert_().about(javaSources())
        .that(asList(model, configClass, R))
        .processedWith(new EpoxyProcessor())
        .compilesWithoutError()
        .and()
        .generatesSources(generatedModel);
  }
}
