package com.airbnb.epoxy.compose.sample

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.compose.*
import com.airbnb.epoxy.compose.sample.epoxyviews.HeaderView
import com.airbnb.epoxy.compose.sample.ui.theme.EpoxyTheme
import com.airbnb.paris.extensions.addDefault
import com.airbnb.paris.extensions.style

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EpoxyInterop {
                HeaderView(this).apply {
                    setTitle("testing")
                    setCaption("Caption measuring things")
                }
            }
        }
    }
}
