package com.airbnb.epoxy


internal fun String.lowerCaseFirstLetter(): String {
    if (isEmpty()) {
        return this
    }

    return Character.toLowerCase(get(0)) + substring(1)
}