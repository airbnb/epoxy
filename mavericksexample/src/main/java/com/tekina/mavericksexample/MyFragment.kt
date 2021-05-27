package com.tekina.mavericksexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.databinding.DataBindingUtil
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.composableInterop
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.tekina.mavericksexample.epoxyviews.headerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class HelloWorldState(
    val title: String = "Hello World",
    val counter: Int = 0,
) : MavericksState

class HelloWorldViewModel(initialState: HelloWorldState) :
    MavericksViewModel<HelloWorldState>(initialState) {
    fun getMoreExcited() = setState { copy(title = "$title!") }

    fun increase() {
        setState {
            copy(counter = counter + 1)
        }
    }
}

class MyFragment : Fragment(R.layout.fragment_my), MavericksView {
    private val viewModel by fragmentViewModel(HelloWorldViewModel::class)

    private val controller: MyEpoxyController by lazy { MyEpoxyController(viewModel) }
    private lateinit var epoxyRecyclerView: EpoxyRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("Aniket2, onCreate")

//        controller =

        controller.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my, container, false).apply {
        epoxyRecyclerView = findViewById(R.id.epoxyRecyclerView)
        epoxyRecyclerView.setController(controller)
//        with(viewModel) {
//            viewModelScope.launch {
//                stateFlow.collect {
//                    println("Aniket2, collecting")
//                    getMoreExcited()
//                }
//            }
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewModelScope.launch {
            viewModel.stateFlow.collect {
                println("Aniket2, it: $it")
            }
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
        println("Aniket2, collecting")

        headerView {
            id("header_id")
            data?.apply {
                title("$counter")
            }

            clickListener { _ ->
                this@MyEpoxyController.viewModel.increase()
            }
        }

//        data?.apply {
//            composableInterop(id = "composable_id") {
//                Text("text from compose: $counter")
//            }
//        }
    }
}