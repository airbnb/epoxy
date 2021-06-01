package com.tekina.mavericksexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.lifecycle.ViewTreeLifecycleOwner
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my, container, false).apply {
        findViewById<EpoxyRecyclerView>(R.id.epoxyRecyclerView).apply {
            setController(controller)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        controller.onSaveInstanceState(outState)
    }

    override fun invalidate() = withState(viewModel) {
        controller.setData(it)
    }
}

class MyEpoxyController(private val viewModel: HelloWorldViewModel) :
    TypedEpoxyController<HelloWorldState>() {

    override fun buildModels(data: HelloWorldState?) {

        composableInterop("test") {
            Text(text = "composableInterop")
        }

//        withState(viewModel) {
//            it.counter.forEachIndexed { index, counterValue ->
//                composableInterop(index.toString()) {
//                    Text(text = "Text from composable interop ${data?.counter?.get(counterValue) ?: "empty"}")
//                }

//                headerView {
//                    id(index)
//                    data?.apply {
//                        title("Text from normal epoxy model: $counterValue")
//                    }
//
//                    clickListener { _ ->
//                        this@MyEpoxyController.viewModel.increase(index)
//                    }
//                }
//            }
//        }
    }
}