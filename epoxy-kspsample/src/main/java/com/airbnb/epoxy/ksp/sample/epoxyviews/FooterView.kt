package com.airbnb.epoxy.ksp.sample.epoxyviews

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.ksp.sample.R

@EpoxyModelClass
abstract class FooterView : EpoxyModel<ConstraintLayout>() {

    @EpoxyAttribute
    lateinit var text: String

    override fun getDefaultLayout(): Int = R.layout.footer_view

    override fun bind(view: ConstraintLayout) {
        super.bind(view)
        view.findViewById<TextView>(R.id.footer_text).text = text
    }
}
