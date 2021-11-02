package com.airbnb.epoxy

import android.content.Context
import android.view.View
import androidx.annotation.Dimension
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class TestManyTypesView(context: Context?) : View(context) {

    @set:ModelProp
    var myProperty: Int = 0

    @set:ModelProp
    var myNullableProperty: Int? = 0

    @set:ModelProp
    var delegatedProperty: Int by Delegates.observable(0) { _, _, _ ->

    }

    @ModelProp(defaultValue = "DEFAULT_ENABLED")
    override fun setEnabled(enabled: Boolean) {
    }

    @ModelProp
    fun setStringValue(value: String) {
    }

    @ModelProp(ModelProp.Option.DoNotHash)
    fun setFunctionType(value: (String, String) -> Integer) {
    }

    @ModelProp
    fun setListOfDataClass(value: List<SomeDataClass>) {
    }

    @ModelProp
    fun setListOfEnumClass(value: List<SomeEnumClass>) {
    }

    @ModelProp
    fun setNullableStringValue(value: String?) {
    }

    @ModelProp
    fun setIntValue(value: Int) {
    }

    @JvmOverloads
    @ModelProp
    fun setIntValueWithDefault(value: Int = 3) {
    }

    @ModelProp
    fun setIntValueWithAnnotation(@StringRes value: Int) {
    }

    @ModelProp
    fun setIntValueWithRangeAnnotation(@IntRange(from = 0, to = TO_RANGE.toLong()) value: Int) {
    }

    @ModelProp
    fun setIntValueWithDimenTypeAnnotation(@Dimension(unit = Dimension.DP) value: Int) {
    }

    @ModelProp
    fun setIntWithMultipleAnnotations(
        @IntRange(
            from = 0,
            to = TO_RANGE.toLong()
        ) @Dimension(unit = Dimension.DP) value: Int
    ) {
    }

    @ModelProp
    fun setIntegerValue(value: Int) {
    }

    @ModelProp
    fun setBoolValue(value: Boolean) {
    }

    @ModelProp
    fun setModels(models: List<EpoxyModel<*>?>) {
    }

    @ModelProp
    fun setBooleanValue(value: Boolean?) {
    }

    @ModelProp
    fun setArrayValue(value: Array<String?>?) {
    }

    @ModelProp
    fun setListValue(value: List<String?>?) {
    }

    @ModelProp(options = [ModelProp.Option.DoNotHash])
    fun setClickListener(value: OnClickListener?) {
    }

    @ModelProp(options = [ModelProp.Option.DoNotHash])
    fun setCustomClickListener(value: CustomClickListenerSubclass?) {
    }

    @ModelProp(options = [ModelProp.Option.GenerateStringOverloads])
    fun setTitle(title: CharSequence?) {
    }

    companion object {
        const val TO_RANGE = 200
        const val DEFAULT_ENABLED = true
    }
}

data class SomeDataClass(
    val title: String
)

enum class SomeEnumClass {
    Foo
}

// Should not be used with modelfactory or "model click listener" code gen as those require the exact
// OnClickListener type.
class CustomClickListenerSubclass : View.OnClickListener {
    override fun onClick(v: View?) {

    }
}