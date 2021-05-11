package com.airbnb.epoxy

/**
 * Used to mark Epoxy model building DSLs so that when using generated kotlin extension functions
 * for building models you cannot incorrectly nest models and also don't see cluttered, incorrect
 * code completion suggestions.
 */
@DslMarker
annotation class EpoxyBuildScope
