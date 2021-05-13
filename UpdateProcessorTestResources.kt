#!/usr/bin/env kscript
@file:DependsOn("org.jsoup:jsoup:1.13.1")

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File


fun main() {
    val testResultHtmlRegex = Regex("/build/reports/tests/.*/classes/.*\\.html")
    File(".")
        .walk()
        .filter { it.isFile }
        .filter { it.path.contains(testResultHtmlRegex) }
        .forEach { updateTestClass(it) }
}

fun updateTestClass(testReport: File) {
    val doc: Document = Jsoup.parse(testReport, "UTF-8")

    // Failing processor tests have their output in a <pre></pre> block
    doc.getElementsByTag("pre")
        .filter { element ->
            // A failing block contains the text "Source declared the same top-level types of an expected source, but
            // didn't match exactly."
            element.text().contains("Source declared the same top-level types of an expected source")
        }.map { it.text() }
        .forEach { failingTestText ->
            updateIndividualTest(failingTestText)
        }
}

private fun updateIndividualTest(failingTestText: String) {
    val expectedFile = expectedFileRegex
        .find(failingTestText)
        ?.groupValues
        ?.getOrNull(1)

        ?.let { filePath ->
            // The test copies the source file to the build folder. We need to modify the original file to update its expected source
            File(
                filePath.replace(
                    "/build/intermediates/sourceFolderJavaResources/debug/",
                    "/src/test/resources/"
                )
            )
        }
        ?.takeIf { it.isFile }
        ?: error("Count not find expected file in $failingTestText")

    // The error message includes the source code that was generated.
    // Actual Source:
    //=================
    // [code here]
    //
    // javaSources was: [com.google.testing.compile.JavaFileObjects$ResourceSourceJavaFileObject[file:/Users/elihart/repos/epoxy/epoxy-modelfactorytest/build/intermediates/sourceFolderJavaResources/debug/GroupPropMultipleSupportedAttributeDifferentNameModelView.java]]
    // at com.airbnb.epoxy.ProcessorTestUtils.assertGeneration(ProcessorTestUtils.kt:33)
    // ...

    val actualSource = failingTestText.substringAfter(
        """
            Actual Source:
            =================

        """.trimIndent()
    ).substringBefore("javaSources was:")
        .substringBefore("object was:")

    expectedFile.writeText(actualSource)

    println("Updated test source ${expectedFile.path.substringAfter("/epoxy/")}")
}

// We expect to see a line like:
// Expected file: </Users/elihart/repos/epoxy/epoxy-modelfactorytest/build/intermediates/sourceFolderJavaResources/debug/AllTypesModelViewModel_.java>
// Which tells us where the original processor test file lives
val expectedFileRegex = Regex("Expected file: <(.*)>")
