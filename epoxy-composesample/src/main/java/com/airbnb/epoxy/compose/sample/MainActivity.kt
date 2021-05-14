package com.airbnb.epoxy.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.ExpoxyInterop
import com.airbnb.epoxy.composableInterop
import com.airbnb.epoxy.compose.sample.epoxyviews.HeaderViewModel_
import com.airbnb.epoxy.compose.sample.epoxyviews.headerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: EpoxyRecyclerView = findViewById(R.id.interop_recycler_view)
        val composeView: ComposeView = findViewById(R.id.compose_view)

        recyclerView.withModels {
            headerView {
                id("header")
                title("Testing Composable in Epoxy")
            }

            for (i in 0..100) {
                composableInterop(
                    id = "compose_text_$i",
                    composeFunction = {
                        ShowCaption(
                            modifier = Modifier
                                .background(color = Color.Yellow)
                                .padding(16.dp)
                        )
                    }
                )

                composableInterop(
                    id = "news_$i",
                    composeFunction = { NewsStory() }
                )
            }
        }

        composeView.setContent {
            Text(text = "Testing Epoxy in Composable")
            LazyColumn {
                items(100) { index ->
                    ExpoxyInterop<HeaderViewModel_> {
                        id("id_header_view_model_$index")
                        title("Epoxy model in compose $index")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun ShowCaption(modifier: Modifier = Modifier) {
    Text(
        text = "Caption coming from composable",
        color = Color.DarkGray,
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
