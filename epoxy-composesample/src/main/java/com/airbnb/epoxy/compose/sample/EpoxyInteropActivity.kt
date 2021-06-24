package com.airbnb.epoxy.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.epoxy.EpoxyInterop
import com.airbnb.epoxy.compose.sample.epoxyviews.HeaderViewModel_

class EpoxyInteropActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epoxy_interop)

        val composeView: ComposeView = findViewById(R.id.compose_view)

        composeView.setContent {
            Column {
                Title("Testing Epoxy in Composable")

                LazyColumn {
                    items(100) { index ->
                        EpoxyInterop<HeaderViewModel_>(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            id("id_header_view_model", index.toLong())
                            title("Epoxy model in compose $index")
                            showImage(true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Title(titleText: String) {
    Text(
        text = titleText,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .background(Color(0x85DFDFFF), RectangleShape)
            .padding(20.dp)
            .fillMaxWidth()
    )
}
