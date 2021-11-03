package com.airbnb.epoxy.processor

import androidx.room.compiler.processing.XProcessingEnv
import androidx.room.compiler.processing.XTypeElement
import com.airbnb.epoxy.processor.resourcescanning.ResourceScanner
import com.squareup.javapoet.ClassName
import kotlin.math.min

class DataBindingModuleLookup(
    private val environment: XProcessingEnv,
    private val logger: Logger,
    private val resourceProcessor: ResourceScanner
) {
    fun getModuleName(element: XTypeElement): String {
        val packageName = element.packageName

        // First we try to get the module name by looking at what R classes were found when processing
        // layout annotations. This may find nothing if no layouts were given as annotation params
        var moduleName: String? = getModuleNameViaResources(packageName)
        if (moduleName == null) {
            // If the first approach fails, we try to guess at the R class for the module and look up
            // the class to see if it exists. This can fail if this model's package name does not
            // include the module name as a prefix (convention makes this unlikely.)
            moduleName = getModuleNameViaGuessing(packageName)
        }

        if (moduleName == null) {
            logger.logError("Could not find module name for DataBinding BR class.")
            // Fallback to using the package name so we can at least try to generate and compile something
            moduleName = packageName
        }

        return moduleName
    }

    /**
     * Attempts to get the module name of the given package. We can do this because the package name
     * of an R class is the module. Generally only one R class is used and we can just use that module
     * name, but it is possible to have multiple R classes. In that case we compare the package names
     * to find what is the most similar.
     *
     *
     * We need to get the module name to know the path of the BR class for data binding.
     */
    private fun getModuleNameViaResources(packageName: String): String {
        val rClasses = resourceProcessor.rClassNames
        if (rClasses.isEmpty()) {
            return packageName
        }
        if (rClasses.size == 1) {
            // Common case
            return rClasses[0].packageName()
        }

        // Generally the only R class used should be the app's. It is possible to use other R classes
        // though, like Android's. In that case we figure out the most likely match by comparing the
        // package name.
        //  For example we might have "com.airbnb.epoxy.R" and "android.R"
        val packageNames = packageName.split("\\.").toTypedArray()
        var bestMatch: ClassName? = null
        val bestNumMatches = -1
        for (rClass in rClasses) {
            val rModuleNames = rClass.packageName().split("\\.").toTypedArray()
            var numNameMatches = 0
            for (i in 0 until min(packageNames.size, rModuleNames.size)) {
                if (packageNames[i] == rModuleNames[i]) {
                    numNameMatches++
                } else {
                    break
                }
            }
            if (numNameMatches > bestNumMatches) {
                bestMatch = rClass
            }
        }
        return bestMatch!!.packageName()
    }

    /**
     * Attempts to get the android module that is currently being processed.. We can do this because
     * the package name of an R class is the module name. So, we take any element in the module,
     *
     *
     * We need to get the module name to know the path of the BR class for data binding.
     */
    private fun getModuleNameViaGuessing(packageName: String): String? {
        val packageNameParts = packageName.split("\\.").toTypedArray()
        var moduleName = ""
        for (i in packageNameParts.indices) {
            moduleName += packageNameParts[i]
            val rClass = environment.findType("$moduleName.R")
            moduleName += if (rClass != null) {
                return moduleName
            } else {
                "."
            }
        }
        return null
    }
}
