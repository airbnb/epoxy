package com.airbnb.epoxy.composeinterop.maverickssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.ModelCollector
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.composableInterop
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState

data class MultiKeyComposeInteropState(
    val keyAsInt: Int = 0,
    val keyAsString: String = "",
    val keyAsList: List<Int> = emptyList(),
    val keyAsDataClass: DataClassKey = DataClassKey()
) : MavericksState

data class DataClassKey(
    var listInDataClass: MutableList<Int> = mutableListOf()
)

class MultiKeyComposeInteropViewModel(initialState: MultiKeyComposeInteropState) :
    MavericksViewModel<MultiKeyComposeInteropState>(initialState) {

    fun increaseCounter() {
        withState { state ->
            val updatedCounter = state.keyAsInt + 1
            setState { copy(keyAsInt = updatedCounter) }
        }
    }

    fun appendKeyString() {
        withState { state ->
            val updatedString = state.keyAsString + "#"
            setState { copy(keyAsString = updatedString) }
        }
    }

    fun appendKeyList() {
        withState { state ->
            val updatedList = state.keyAsList.toMutableList()
            updatedList.add(1)
            setState { copy(keyAsList = updatedList) }
        }
    }

    fun appendDataClass() {
        withState { state ->
            val updatedDataClass =
                state.keyAsDataClass.copy(listInDataClass = state.keyAsDataClass.listInDataClass.toMutableList())

            updatedDataClass.listInDataClass.add(2)

            setState { copy(keyAsDataClass = updatedDataClass) }
        }
    }

    val mutableCounter = mutableStateOf(0)
    var recomposingForWithoutKeyMutableState = 0

    var recomposingForWithoutKeyInt = 0
    var recomposingForWithKeyInt = 0

    var recomposingForWithoutKeyString = 0
    var recomposingForWithKeyString = 0

    var recomposingForWithoutKeyList = 0
    var recomposingForWithKeyList = 0

    var recomposingForWithoutKeyDataClass = 0
    var recomposingForWithKeyDataClass = 0

    fun increaseMutableState() {
        mutableCounter.value++
        println("counter.value: ${mutableCounter.value}")
    }
}

class MultiKeyComposeInteropFargment :
    Fragment(R.layout.fragment_multi_key_compose_interop),
    MavericksView {
    private val viewModel by fragmentViewModel(MultiKeyComposeInteropViewModel::class)

    private val controller: MultiKeyComposeInteropEpoxyController by lazy {
        MultiKeyComposeInteropEpoxyController(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        inflater.inflate(R.layout.fragment_multi_key_compose_interop, container, false).apply {
            findViewById<EpoxyRecyclerView>(R.id.epoxyRecyclerView).apply {
                setController(controller)
            }
        }

    override fun invalidate() = withState(viewModel) {
        controller.setData(it)
    }
}

class MultiKeyComposeInteropEpoxyController(private val viewModel: MultiKeyComposeInteropViewModel) :
    TypedEpoxyController<MultiKeyComposeInteropState>() {

    override fun buildModels(state: MultiKeyComposeInteropState) {
        withMutableState()

        withoutInteropKeyInt(state)
        withInteropKeyInt(state)

        withoutInteropKeyString(state)
        withInteropKeyString(state)

        withoutInteropKeyList(state)
        withInteropKeyList(state)

        withoutInteropKeyDataClass(state)
        withInteropKeyDataClass(state)
    }

    @Composable
    fun InteropDivider() {
        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
        )
    }

    @Composable
    fun ComposableWithMutableState() {
        viewModel.recomposingForWithoutKeyMutableState++

        ClickableText(
            text = AnnotatedString("withoutInteropKey mutableState: ${viewModel.mutableCounter.value}, recomposingCount: ${viewModel.recomposingForWithoutKeyMutableState}"),
            onClick = {
                viewModel.increaseMutableState()
            }
        )
    }

    @Composable
    fun ComposableTextWithoutKeyInt(counter: Int) {
        viewModel.recomposingForWithoutKeyInt++

        ClickableText(
            text = AnnotatedString("withoutInteropKey Int: $counter, recomposingCount: ${viewModel.recomposingForWithoutKeyInt}"),
            onClick = {
                viewModel.increaseCounter()
            }
        )
    }

    @Composable
    fun ComposableTextWithKeyInt(counter: Int) {
        viewModel.recomposingForWithKeyInt++

        ClickableText(
            text = AnnotatedString("withInteropKey Int: $counter, recomposingCount: ${viewModel.recomposingForWithKeyInt}"),
            onClick = {
                viewModel.increaseCounter()
            }
        )
    }

    @Composable
    fun ComposableTextWithoutKeyString(keyAsString: String) {
        viewModel.recomposingForWithoutKeyString++

        ClickableText(
            text = AnnotatedString("withoutInteropKey String: $keyAsString, recomposingCount: ${viewModel.recomposingForWithoutKeyString}"),
            onClick = {
                this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyString()
            }
        )
    }

    @Composable
    fun ComposableTextWithKeyString(keyAsString: String) {
        viewModel.recomposingForWithKeyString++

        ClickableText(
            text = AnnotatedString("withInteropKey String: $keyAsString, recomposingCount: ${viewModel.recomposingForWithKeyString}"),
            onClick = {
                this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyString()
            }
        )
    }

    @Composable
    fun ComposableWithoutInteropKeyList(keyAsList: List<Int>) {
        viewModel.recomposingForWithoutKeyList++

        ClickableText(
            text = AnnotatedString("withoutInteropKey List: $keyAsList, recomposingCount: ${viewModel.recomposingForWithoutKeyList}"),
            onClick = {
                this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyList()
            }
        )
    }

    @Composable
    fun ComposableWithInteropKeyList(keyAsList: List<Int>) {
        viewModel.recomposingForWithKeyList++

        ClickableText(
            text = AnnotatedString("withInteropKey List: $keyAsList, recomposingCount: ${viewModel.recomposingForWithKeyList}"),
            onClick = {
                this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyList()
            }
        )
    }

    @Composable
    fun ComposableWithoutInteropKeyDataClass(keyAsDataClass: DataClassKey) {
        viewModel.recomposingForWithoutKeyDataClass++

        ClickableText(
            text = AnnotatedString("withoutInteropKey DataClass: $keyAsDataClass, recomposingCount: ${viewModel.recomposingForWithoutKeyDataClass}"),
            onClick = {
                this@MultiKeyComposeInteropEpoxyController.viewModel.appendDataClass()
            }
        )
    }

    @Composable
    fun ComposableWithInteropKeyDataClass(keyAsDataClass: DataClassKey) {
        viewModel.recomposingForWithKeyDataClass++

        ClickableText(
            text = AnnotatedString("withInteropKey DataClass: $keyAsDataClass, recomposingCount: ${viewModel.recomposingForWithKeyDataClass}"),
            onClick = {
                this@MultiKeyComposeInteropEpoxyController.viewModel.appendDataClass()
            }
        )
    }

    private fun ModelCollector.withMutableState() {
        composableInterop("id_counter_for_mutableState") {
            Column {
                this@MultiKeyComposeInteropEpoxyController.ComposableWithMutableState()
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyInt(state: MultiKeyComposeInteropState) {
        composableInterop("id_counter_without_keys") {
            this@MultiKeyComposeInteropEpoxyController.ComposableTextWithoutKeyInt(state.keyAsInt)
        }
    }

    private fun ModelCollector.withInteropKeyInt(state: MultiKeyComposeInteropState) {
        composableInterop("id_counter_with_keys", state.keyAsInt) {
            Column {
                this@MultiKeyComposeInteropEpoxyController.ComposableTextWithKeyInt(state.keyAsInt)
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyString(state: MultiKeyComposeInteropState) {
        composableInterop("id_withoutInteropKeyString") {
            this@MultiKeyComposeInteropEpoxyController.ComposableTextWithoutKeyString(state.keyAsString)
        }
    }

    private fun ModelCollector.withInteropKeyString(state: MultiKeyComposeInteropState) {
        composableInterop("id_withInteropKeyString", state.keyAsString) {
            Column {
                this@MultiKeyComposeInteropEpoxyController.ComposableTextWithKeyString(state.keyAsString)
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyList(state: MultiKeyComposeInteropState) {
        composableInterop("id_withoutInteropKeyList") {
            this@MultiKeyComposeInteropEpoxyController.ComposableWithoutInteropKeyList(state.keyAsList)
        }
    }

    private fun ModelCollector.withInteropKeyList(state: MultiKeyComposeInteropState) {
        composableInterop("id_withInteropKeyList", state.keyAsList) {
            Column {
                this@MultiKeyComposeInteropEpoxyController.ComposableWithInteropKeyList(state.keyAsList)
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyDataClass(state: MultiKeyComposeInteropState) {
        composableInterop("id_withoutInteropKeyDataClass") {
            this@MultiKeyComposeInteropEpoxyController.ComposableWithoutInteropKeyDataClass(state.keyAsDataClass)
        }
    }

    private fun ModelCollector.withInteropKeyDataClass(state: MultiKeyComposeInteropState) {
        composableInterop("id_withInteropKeyDataClass", state.keyAsDataClass) {
            Column {
                this@MultiKeyComposeInteropEpoxyController.ComposableWithInteropKeyDataClass(state.keyAsDataClass)
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }
}
