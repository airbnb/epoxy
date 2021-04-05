package com.airbnb.epoxy.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.compose.composableInterop
import com.airbnb.epoxy.compose.sample.epoxyviews.headerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: EpoxyRecyclerView = findViewById(R.id.interop_recycler_view)

        recyclerView.withModels {
            headerView {
                id("header")
                title("Testing Composable in Epoxy")
            }

            composableInterop(
                id = "compose_text",
                composeFunction = {
                    ShowCaption(
                        modifier =
                        Modifier.background(color = Color.Blue)
                            .padding(16.dp)
                    )
                }
            )

            composableInterop(
                id = "news",
                composeFunction = { NewsStory() }
            )
        }
    }
}

@Composable
@Preview
fun ShowCaption(modifier: Modifier = Modifier) {
    Text(
        text = "Caption coming from composable",
        color = Color.Yellow,
        modifier = modifier
    )
}

@Composable
@Preview
fun NewsStory() {
    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.header),
            contentDescription = null
        )
        Text("A day in Shark fin Cove")
        Text("Davenport, California")
        Text("December 2021")
    }
}
