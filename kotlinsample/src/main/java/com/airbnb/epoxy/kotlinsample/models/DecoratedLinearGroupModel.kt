package com.airbnb.epoxy.kotlinsample.models

import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.GroupModel
import com.airbnb.epoxy.kotlinsample.R

@EpoxyModelClass
abstract class DecoratedLinearGroupModel : GroupModel(R.layout.decorated_linear_group)
