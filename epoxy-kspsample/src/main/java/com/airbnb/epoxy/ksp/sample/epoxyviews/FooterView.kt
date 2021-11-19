package com.airbnb.epoxy.ksp.sample.epoxyviews

import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.ksp.sample.R

@EpoxyModelClass
abstract class FooterView : EpoxyModel<LinearLayout>() {

    @EpoxyAttribute
    lateinit var text: String

    override fun getDefaultLayout(): Int = R.layout.footer_view

    override fun bind(view: LinearLayout) {
        super.bind(view)
        view.findViewById<TextView>(R.id.footer_text).text = text
    }
}
