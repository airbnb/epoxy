package com.airbnb.epoxy.processor

import com.squareup.javapoet.TypeName

data class ControllerModelField(
    val fieldName: String,
    var typeName: TypeName,
    val packagePrivate: Boolean
)
