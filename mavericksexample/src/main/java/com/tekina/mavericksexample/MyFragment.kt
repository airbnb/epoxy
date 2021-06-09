package com.tekina.mavericksexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.composableInterop
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.tekina.mavericksexample.epoxyviews.headerView

data class HelloWorldState(
    val counter: List<Int> = List(100) { it },
) : MavericksState

class HelloWorldViewModel(initialState: HelloWorldState) :
    MavericksViewModel<HelloWorldState>(initialState) {

    fun increase(index: Int) {
        withState { state ->
            val updatedCounterList =
                state.counter.mapIndexed { i, value -> if (i == index) value + 1 else value }

            setState {
                copy(counter = updatedCounterList)
            }
        }
    }
}

class MyFragment : Fragment(R.layout.fragment_my), MavericksView {
    private val viewModel by fragmentViewModel(HelloWorldViewModel::class)

    private val controller: MyEpoxyController by lazy { MyEpoxyController(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my, container, false).apply {
        findViewById<EpoxyRecyclerView>(R.id.epoxyRecyclerView).apply {
            setController(controller)
        }
    }

    override fun invalidate() = withState(viewModel) {
        controller.setData(it)
    }
}

class MyEpoxyController(private val viewModel: HelloWorldViewModel) :
    TypedEpoxyController<HelloWorldState>() {

    private fun annotatedString(str: String) = buildAnnotatedString {
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append(str)
        }
    }

    override fun buildModels(data: HelloWorldState?) {
        data?.counter?.forEachIndexed { index, counterValue ->
            composableInterop("$index") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextFromCompose(counterValue) {
                        this@MyEpoxyController.viewModel.increase(index)
                    }
                }
            }

            headerView {
                id(index)
                title("Text from normal epoxy model: $counterValue")

                clickListener { _ ->
                    this@MyEpoxyController.viewModel.increase(index)
                }
            }
        }
    }

    @Composable
    fun TextFromCompose(counterValue: Int, onClick: () -> Unit) {
        ClickableText(
            text = annotatedString("Text from composable interop $counterValue"),
            onClick = {
                onClick()
            }
        )
    }
}