package com.airbnb.epoxy.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.composableInterop
import com.airbnb.epoxy.compose.sample.epoxyviews.headerView

class ComposableInteropActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_composable_interop)

        val recyclerView: EpoxyRecyclerView = findViewById(R.id.epoxy_recycler_view)

        recyclerView.withModels {
            headerView {
                id("header")
                title("Testing Composable in Epoxy")
            }

            for (i in 0..100) {
                composableInterop(id = "compose_text_$i") {
                    ShowCaption("Caption coming from composable")
                }

                composableInterop(id = "news_$i") {
                    NewsStory()
                }
            }
        }
    }
}

@Composable
@Preview
fun NewsStory() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = null
        )

        ShowCaption("Above Image and below text are coming from Compose in Epoxy")
        ShowCaption("Davenport, California")
        ShowCaption("December 2021")

        Divider(
            color = Color(0x859797CF),
            thickness = 2.dp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ShowCaption(text: String) {
    Text(
        text,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    )
}
