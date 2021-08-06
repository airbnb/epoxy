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
    val counter: Int = 0,
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
        val data = DataClassKey()
        withState { state ->
            val updatedCounter = state.counter + 1
            setState { copy(counter = updatedCounter) }
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

    private fun ModelCollector.withoutInteropKeyInt(state: MultiKeyComposeInteropState) {
        composableInterop("id_counter_without_keys") {
            ClickableText(
                text = AnnotatedString("withoutInteropKey Int: ${state.counter}"),
                onClick = {
                    this@MultiKeyComposeInteropEpoxyController.viewModel.increaseCounter()
                }
            )
        }
    }

    private fun ModelCollector.withInteropKeyInt(state: MultiKeyComposeInteropState) {
        composableInterop("id_counter_with_keys", state.counter) {
            Column {
                ClickableText(
                    text = AnnotatedString("withInteropKey Int: ${state.counter}"),
                    onClick = {
                        this@MultiKeyComposeInteropEpoxyController.viewModel.increaseCounter()
                    }
                )
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyString(state: MultiKeyComposeInteropState) {
        composableInterop("id_withoutInteropKeyString") {
            ClickableText(
                text = AnnotatedString("withoutInteropKey String: ${state.keyAsString}"),
                onClick = {
                    this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyString()
                }
            )
        }
    }

    private fun ModelCollector.withInteropKeyString(state: MultiKeyComposeInteropState) {
        composableInterop("id_withInteropKeyString", state.keyAsString) {
            Column {
                ClickableText(
                    text = AnnotatedString("withInteropKey String: ${state.keyAsString}"),
                    onClick = {
                        this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyString()
                    }
                )
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyList(state: MultiKeyComposeInteropState) {
        composableInterop("id_withoutInteropKeyList") {
            ClickableText(
                text = AnnotatedString("withoutInteropKey List: ${state.keyAsList}"),
                onClick = {
                    this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyList()
                }
            )
        }
    }

    private fun ModelCollector.withInteropKeyList(state: MultiKeyComposeInteropState) {
        composableInterop("id_withInteropKeyList", state.keyAsList) {
            Column {
                ClickableText(
                    text = AnnotatedString("withInteropKey List: ${state.keyAsList}"),
                    onClick = {
                        this@MultiKeyComposeInteropEpoxyController.viewModel.appendKeyList()
                    }
                )
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }

    private fun ModelCollector.withoutInteropKeyDataClass(state: MultiKeyComposeInteropState) {
        composableInterop("id_withoutInteropKeyDataClass") {
            ClickableText(
                text = AnnotatedString("withoutInteropKey DataClass: ${state.keyAsDataClass}"),
                onClick = {
                    this@MultiKeyComposeInteropEpoxyController.viewModel.appendDataClass()
                }
            )
        }
    }

    private fun ModelCollector.withInteropKeyDataClass(state: MultiKeyComposeInteropState) {
        composableInterop("id_withInteropKeyDataClass", state.keyAsDataClass) {
            Column {
                ClickableText(
                    text = AnnotatedString("withInteropKey DataClass: ${state.keyAsDataClass}"),
                    onClick = {
                        this@MultiKeyComposeInteropEpoxyController.viewModel.appendDataClass()
                    }
                )
                this@MultiKeyComposeInteropEpoxyController.InteropDivider()
            }
        }
    }
}
