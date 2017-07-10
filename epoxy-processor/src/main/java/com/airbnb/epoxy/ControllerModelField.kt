package com.airbnb.epoxy

import com.squareup.javapoet.TypeName

import javax.lang.model.element.Element

import com.airbnb.epoxy.Utils.isFieldPackagePrivate

class ControllerModelField(element: Element) {

    var fieldName: String = element.simpleName.toString()
    var typeName: TypeName = TypeName.get(element.asType())
    var packagePrivate: Boolean = isFieldPackagePrivate(element)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ControllerModelField

        if (fieldName != other.fieldName) return false
        if (typeName != other.typeName) return false
        if (packagePrivate != other.packagePrivate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fieldName.hashCode()
        result = 31 * result + typeName.hashCode()
        result = 31 * result + if (packagePrivate) 1 else 0
        return result
    }

    override fun toString() = "ControllerModelField(fieldName='$fieldName', typeName=$typeName, packagePrivate=$packagePrivate)"
}
