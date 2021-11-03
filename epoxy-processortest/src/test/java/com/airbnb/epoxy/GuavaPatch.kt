@file:JvmName("GuavaPatch")
package com.airbnb.epoxy

import java.io.File
import java.net.URL

/**
 * Since AGP 3.6.0, the class-loader behavior has been modified.
 * Unfortunately Guava (via compile-testing) uses a class-loader based mechanism
 * which is valid on JVM but not supposed to be supported on Android.
 * As the project paths are simple enough, we can hardcode them for now.
 */
fun String.patchResource(): URL =
    File("build/intermediates/sourceFolderJavaResources/debug/$this").toURI().toURL()

fun File.unpatchResource(): File = File(
    canonicalPath.replace(
        "build/intermediates/sourceFolderJavaResources/debug/",
        "src/test/resources/"
    )
)
