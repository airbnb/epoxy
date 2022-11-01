package com.airbnb.epoxy.composeinterop.maverickssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.composableInterop
import com.airbnb.epoxy.composeinterop.maverickssample.ComposeInteropGridFragment.Companion.SPAN_COUNT
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState

/**
 * Example fragment showing how to change span count when using compose epoxy interop
 */
class ComposeInteropGridFragment : Fragment(R.layout.fragment_my), MavericksView {
    private val viewModel by fragmentViewModel(HelloWorldViewModel::class)

    private val controller: MyGridEpoxyController by lazy { MyGridEpoxyController(viewModel) }

    private val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(requireContext(), SPAN_COUNT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_my, container, false).apply {
        findViewById<EpoxyRecyclerView>(R.id.epoxyRecyclerView).apply {
            layoutManager = gridLayoutManager
            setController(controller)
        }
    }

    override fun invalidate() = withState(viewModel) {
        controller.setData(it)
    }

    companion object {
        const val SPAN_COUNT = 2
    }
}

class MyGridEpoxyController(private val viewModel: HelloWorldViewModel) :
    TypedEpoxyController<CounterState>() {

    private fun annotatedString(str: String) = buildAnnotatedString {
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append(str)
        }
    }

    override fun buildModels(state: CounterState) {
        // overriding span scount to take full width in grid, default span count is 1
        state.counter.forEachIndexed { index, counterValue ->
            composableInterop("$index", counterValue, spanSize = SPAN_COUNT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextFromCompose(counterValue) {
                        this@MyGridEpoxyController.viewModel.increase(index)
                    }
                }
            }
        }
    }

    @Composable
    fun TextFromCompose(counterValue: Int, onClick: () -> Unit) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = ""
            )
            ClickableText(
                text = annotatedString("Text from composable interop $counterValue"),
                onClick = {
                    onClick()
                }
            )
        }
    }
}
