package com.airbnb.epoxy.kotlinsample

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.kotlinsample.models.ItemCustomViewModel_
import com.airbnb.epoxy.kotlinsample.models.ItemDataClass
import com.airbnb.epoxy.kotlinsample.models.ItemEpoxyHolder_
import com.airbnb.epoxy.kotlinsample.models.itemCustomView
import com.airbnb.epoxy.kotlinsample.models.itemEpoxyHolder

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: EpoxyRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity)

        recyclerView = findViewById(R.id.recycler_view)


        findViewById<TabLayout>(R.id.tab_layout).addOnTabSelectedListener(object :
                                                                              TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showMixAndMatch()
                    1 -> showDataBindingItems()
                    2 -> showCustomViewItems()
                    3 -> showViewHolder()
                    4 -> showDataClass()
                }
            }
        })

        showMixAndMatch()
    }

    private fun showMixAndMatch() {
        recyclerView.withModels {

            for(i in 0 until 100) {
                dataBindingItem {
                    id("data binding $i")
                    text("this is a data binding model")
                    onclick { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
                    }
                }

                itemCustomView {
                    id("custom view $i")
                    color(Color.GREEN)
                    title("this is a green custom view item")
                    listener { _ ->
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG).show()
                    }
                }

                itemEpoxyHolder {
                    id("view holder $i")
                    title("this is a View Holder item")
                    listener {
                        Toast.makeText(this@MainActivity, "clicked", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                // Since data classes do not use code generation, there's no extension generated here
                ItemDataClass("this is a Data Class Item")
                    .id("data class $i")
                    .addTo(this)
            }
        }
    }

    private fun showDataBindingItems() {
        recyclerView.buildModelsWith {
            for (i in 0 until 100) {
                val title = "Data Binding Item $i"
                DataBindingItemBindingModel_()
                    .text(title)
                    .id(i)
                    .onclick { _ ->
                        Toast.makeText(this@MainActivity, title, Toast.LENGTH_LONG).show()
                    }
                    .addTo(it)
            }
        }
    }

    private fun showCustomViewItems() {
        recyclerView.buildModelsWith {
            for (i in 0 until 100) {
                val title = "Custom View Item $i"
                ItemCustomViewModel_()
                    .color(Color.BLACK)
                    .title("$i times")
                    .id(i)
                    .listener { _ ->
                        Toast.makeText(this@MainActivity, title, Toast.LENGTH_LONG).show()
                    }
                    .addTo(it)
            }
        }
    }

    private fun showViewHolder() {
        recyclerView.buildModelsWith {
            for (i in 0 until 100) {
                val title = "View Holder Item $i"
                ItemEpoxyHolder_()
                    .title(title)
                    .id(i)
                    .listener {
                        Toast.makeText(this@MainActivity, title, Toast.LENGTH_LONG).show()
                    }
                    .addTo(it)
            }
        }
    }

    private fun showDataClass() {
        val modelList = (0 until 100).map { ItemDataClass("Data Class Item $it").id(it) }

        recyclerView.setModels(modelList)
    }

}

fun EpoxyRecyclerView.withModels(buildModelsCallback: EpoxyController.() -> Unit) {
    setControllerAndBuildModels(object : EpoxyController() {
        override fun buildModels() {
            buildModelsCallback()
        }
    })
}

