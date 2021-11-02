# 5.0.0
This adds support for Kotlin Symbol Processing, while maintaining backwards compatibility with java annotation processing via the xprocessing library from Room.

This includes a major version bump to 5.0.0 because there may be slight behavior differences with KSP, especially for generic types in generated code. For example, if you previously had an epoxy attribute in java source code with a raw type it may now appear in the generated code with a wildcard type, which may require tweaking the type that is passed to the model.

Additionally, some type checking was improved, for example more accurate validation of proper equals and hashcode implementations.

To use Epoxy with KSP, simply apply it with the ksp gradle plugin instead of kapt (https://github.com/google/ksp/blob/main/docs/quickstart.md). See the new epoxy-kspsample module for an example.

Note that unfortunately the databinding processor does NOT support KSP, simply because Android databinding itself uses KAPT and KSP cannot currently depend on KAPT sources. The code changes are in place to enable KSP with databinding once the databinding plugin from Android supports KSP (although this is unlikely) - alternatively it may be possible to configure the KSP plugin to run after KAPT and depend on its outputs (you're on your own if you want to try that).

Also, parallel processing support was removed because it is not compatible with KSP.

# 4.6.4 (September 23, 2021)
- Clean up dependency for the experimental epoxy module

# 4.6.3 (September 11, 2021)
- Add EpoxyModel#preBind hook(#1225)
- Add unbind extension to ItemViewBindingEpoxyHolder (#1223)
- Add missing loadStateFlow to PagingDataEpoxyController (#1209)

# 4.6.2 (June 11, 2021)
Fix Drag n Drop not working in 4.6.1 (#1195)

# 4.6.1 (May 13, 2021)
Adds "epoxyDisableDslMarker" annotation processor flag which you can use to delay migration to the model building scope DLSMarker introduced in 4.6.0 if it is a large breaking change for your project.

Note that this only applies to your project modules that you apply it to, and does not apply to the handful of models that ship with the Epoxy library (like the Carousel or `group` builder).

For example:
```groovy
project.android.buildTypes.all { buildType ->
    buildType.javaCompileOptions.annotationProcessorOptions.arguments =
            [
                    epoxyDisableDslMarker     : "true",
            ]
}
```

# 4.6.0 (May 12, 2021)
- View Binder Support (#1175) Bind epoxy models to views outside of a RecyclerView.

### Potentially Breaking
- Use kotlin dsl marker for model building receivers (#1180)

This change uses Kotlin's DSL marker annotation to enforce proper usage of model building extension
functions. You may now need to change some references in your model building code to explicitly reference properties with `this`.

# 4.5.0 (April 13, 2021)
- Fix generated code consistency in builder interfaces (#1166)
- Provided support to invalidate `modelCache` in `PagingDataEpoxyController` (#1161)
- Explicitly add public modifier (#1162)
- Unwrap context to find parent activity in order to share viewpool when using Hilt (#1157)

# 4.4.4 (Mar 24, 2021)
- Provide support for snapshot() function in PagingDataEpoxyController (#1144)

# 4.4.3 (Mar 17, 2021)
- Fixed interface model related regression introduced in the previous release.

# 4.4.2 (Mar 1, 2021)
- Updated package name of the model class generated for an interface

# 4.4.1 (Feb 22, 2021)
- Support for Paging3 (#1126) (Thanks to @osipxd and @anhanh11001!)
- Update KotlinPoet to 1.7.2 (#1117)

# 4.4.0 (Feb 18, 2021)
Bad release, don't use

# 4.3.1 (Dec 2, 2020)
- Fix ANR and view pool resolution in nested group (#1101)

# 4.3.0 (Dec 1, 2020)
- ModelGroupHolder get recycle pool from parent (#1097)
- Add support for `EpoxyModelGroup` in the `EpoxyVisibilityTracker` (#1091)
- Convert EpoxyVisibilityTracker code to Kotlin (#1090)

## Breaking Changes
Note that due to the conversion of EpoxyVisibilityTracker to kotlin you now need to access `EpoxyVisibilityTracker.partialImpressionThresholdPercentage` as a property
`epoxyVisibilityTracker.setPartialImpressionThresholdPercentage(value)` -> `epoxyVisibilityTracker.partialImpressionThresholdPercentage = value`

Also, the ModelGroupHolder improvement required the `ModelGroupHolder#createNewHolder` function to change its signature to accept a `ViewParent` parameter.

If you override `createNewHolder()` anywhere you will need to change it to `createNewHolder(@NonNull ViewParent parent)`

# 4.2.0 (Nov 11, 2020)
- Add notify model changed method (#1063)
- Update to Kotlin 1.4.20-RC and remove dependency on kotlin-android-extensions

# 4.1.0 (Sept 17, 2020)
- Fix some synchronization issues with the parallel Epoxy processing option
- Add view visibility checks to EpoxyVisibilityItem and decouple RecyclerView #1052

# 4.0.0 (Sept 5, 2020)

## New
- Incremental annotation processing for faster builds
- Support for Android Jetpack Paging v3 library in new `epoxy-paging3` artifact
- Model group building with Kotlin DSL (#1012)
- A new annotation processor argument `logEpoxyTimings` can be set to get a detailed breakdown of how long the processors took and where they spent their time (off by default)
- Another new argument `enableParallelEpoxyProcessing` can be set to true to have the annotation processor process annotations and generate files in parallel (via coroutines).

You can enable these processor options in your build.gradle file like so:
```
project.android.buildTypes.all { buildType ->
  buildType.javaCompileOptions.annotationProcessorOptions.arguments =
      [
          logEpoxyTimings  : "true",
          enableParallelEpoxyProcessing     : "true"
      ]
}
```

Parallel processing can greatly speed up processing time (moreso than the incremental support), but given the hairy nature of parallel processing it is still incubating.
Please report any issues or crashes that you notice.
(We are currently using parallel mode in our large project at Airbnb with no problems.)

- Add options to skip generation of functions for getters, reset, and method overloads to reduce generated code
    - New annotation processor options are:
        - epoxyDisableGenerateOverloads
        - epoxyDisableGenerateGetters
        - epoxyDisableGenerateReset


## Fixes
- Synchronize ListUpdateCallback and PagedListModelCache functions (#987)
- Avoid generating bitset checks in models when not needed (reduces code size)
- Fix minor memory leak

## Breaking

- Annotations that previously targeted package elements now target types (classes or interfaces).
  This includes: `EpoxyDataBindingPattern`, `EpoxyDataBindingLayouts`, `PackageModelViewConfig`, `PackageEpoxyConfig`
  This was necessary to work around an incremental annotation processor issue where annotation on package-info elements are not properly recompiled

- In order to enable incremental annotation processing a change had to be made in how the processor of
  `@AutoModel` annotations work. If you use `@AutoModel` in an EpoxyController the annotated Model types
  must be either declared in a different module from the EpoxyController, or in the same module in the same java package.

  Also make sure you have kapt error types enabled.

  However, generally `@AutoModel` is considered legacy and is not recommended. It is a relic of Java Epoxy usage
  and instead the current best practice is to use Kotlin with the Kotlin model extension functions to build models.

- Removed support for generating Epoxy models from Litho components

# 4.0.0-beta6 (July 15, 2020)
- PackageModelViewConfig can now be applied to classes and interfaces in addition to package-info.java

# 4.0.0-beta5 (July 9, 2020)
Fixes:
- An occasional processor crash when the option to log timings is enabled
- Incremental annotation processing of databinding models would fail to generate models (#1014)

Breaking!
- The annotation that support databinding, `EpoxyDataBindingLayouts` and `EpoxyDataBindingPattern`,
must now be placed on a class or interface instead of in a `package-info.java` file. The interface
or class must be in Java, Kotlin is not supported. This is necessary to support incremental processing.

Example usage:
```java
package com.example.app;

import com.airbnb.epoxy.EpoxyDataBindingLayouts;
import com.airbnb.epoxy.EpoxyDataBindingPattern;

@EpoxyDataBindingPattern(rClass = R.class, layoutPrefix = "my_view_prefix")
@EpoxyDataBindingLayouts({R.layout.my_model_layout})
interface EpoxyDataBindingConfig {} 
```

# 4.0.0-beta4 (June 1, 2020)
Fixes:
- Synchronize ListUpdateCallback and PagedListModelCache functions (#987)
- 4.0.0.beta1 generating duplicate method layout(int) #988

# 4.0.0-beta3 (May 27, 2020)
- Sort functions in generated kotlin extension function files deterministically to prevent generated sources from changing
- Avoid generating bitset checks in models when not needed
- Add options to skip generation of functions for getters, reset, and method overloads to reduce generated code

New annotation processor options are:
- epoxyDisableGenerateOverloads
- epoxyDisableGenerateGetters
- epoxyDisableGenerateReset

These can also be controlled (and overridden) on a per package level with the `PackageModelViewConfig` package annotation.

# 4.0.0-beta1 (May 22, 2020)
- Support for incremental annotation processing as an Aggregating processor (#972)
- Removed Litho support
- A new annotation processor argument `logEpoxyTimings` can be set to get a detailed breakdown of how long the processors took and where they spent their time (off by default)
- Another new argument `enableParallelEpoxyProcessing` can be set to true to have the annotation processor process annotations and generate files in parallel (via coroutines).

You can enable these processor options in your build.gradle file like so:
```
project.android.buildTypes.all { buildType ->
  buildType.javaCompileOptions.annotationProcessorOptions.arguments =
      [
          logEpoxyTimings  : "true",
          enableParallelEpoxyProcessing     : "true"
      ]
}
```

Parallel processing can greatly speed up processing time (moreso than the incremental support), but given the nature of parallel processing it is still incubating.
Please report any issues or crashes that you notice.
(We are currently using parallel mode in our large project at Airbnb with no problems.)

## Breaking
In order to enable incremental annotation processing a change had to be made in how the processor of
`@AutoModel` annotations work. If you use `@AutoModel` in an EpoxyController the annotated Model types
must be either declared in a different module from the EpoxyController, or in the same module in the same java package.

Also make sure you have kapt error types enabled.

However, generally `@AutoModel` is considered legacy and is not recommended. It is a relic of Java Epoxy usage
and instead the current best practice is to use Kotlin with the Kotlin model extension functions to build models.
 
# 3.11.0 (May 20, 2020)
- Introduce partial impression visibility states (#973)
- Fix sticky header crash (#976)

# 3.10.0 (May 15, 2020)
- Carousel building with Kotlin DSL (#967)
- Android ViewBinding: added an example in the sample project. (#939)
- Fix setter with default value lookup in kotlin 1.4 (#966)
- Change "result" property name in generated model (#965)
- Add support for Sticky Headers (#842)
- Use measured width/height if it exists in Carousel. (#915)
- Add a getter to EpoxyViewHolder.getHolder(). (#952) (#953)
- Fix visibility tracking during RecyclerView animations (#962)
- Fix leak in ActivityRecyclerPool ((#906)
- Rename ResultCallack to ResultCallback in AsyncEpoxyDiffer (#899)
- Fix incorrect license attributes in POM file (#898)

# 3.9.0 (Dec 17, 2019)
- Fix reading EpoxyDataBindingPattern enableDoNotHash (#837) 
- Make EpoxyRecyclerView.setItemSpacingPx() open (#829)
- Use same version for Mockito Core and Inline (#860)
- Minor documentation and variable name updates. (#870)
- Move epoxy-modelfactory tests to their own module (#834) 
- Remove executable bit from non-executable files (#864)
- Various repo clean ups and version bumps

# 3.8.0 (Sept 16, 2019)
- Add support for Kotlin delegation via annotated interface properties #812
- Fix checked change crash and improve debug errors #806
- Remove extra space in Kotlin extensions #777
- Update project to AGP 3.5, Kotlin 1.3.50, Gradle 5.6

# 3.7.0 (July 1, 2019)
- **New** Add a method to request visibility check externally (https://github.com/airbnb/epoxy/pull/775)

# 3.6.0 (June 18, 2019)
- **New** Preloader system with glide extensions https://github.com/airbnb/epoxy/pull/766
- **Fixed** model click listener crashing on nested model https://github.com/airbnb/epoxy/pull/767

# 3.5.1 (May 21, 2019)
- Bumped Kotlin to 1.3.31

# 3.5.0 (May 8, 2019)
- **New** Converted EpoxyRecyclerView to Kotlin (you may need to update your usage for this). Also added built in support for `EpoxyRecyclerView#withModels` for easy inline model building with Kotlin.
- **Fixed** Crashes in visibility tracking

# 3.4.2 (April 18, 2019)
- **Fixed** Kotlin default param handling had issues with overloaded functions

# 3.4.1 (April 16, 2019)
- **New** Support kotlin default parameters in @ModelView classes (https://github.com/airbnb/epoxy/pull/722)

# 3.4.0 (April 10, 2019)
- **New** Generate OnModelCheckedChangeListener override for props of type `CompoundButton.OnCheckedChangeListener` (https://github.com/airbnb/epoxy/pull/725)
- **New** Extract ID generation methods to new public IdUtils class (https://github.com/airbnb/epoxy/pull/724)
- **Changed** Reset controller state on failed model build (https://github.com/airbnb/epoxy/pull/720)
- **Changed** Disabled the auto-detach behavior on Carousels by default (https://github.com/airbnb/epoxy/pull/688)

# 3.3.0 (Feb 5, 2019)
- **Fixed** Two issues related to the recent EpoxyModelGroup changes (https://github.com/airbnb/epoxy/pull/676)

# 3.2.0 (Jan 21, 2019)
- **New** Enable recycling of views within EpoxyModelGroup (https://github.com/airbnb/epoxy/pull/657)
- **New** Add support to tracking visibility in nested RecyclerViews (https://github.com/airbnb/epoxy/pull/633)
- **New** Add method to clear cache in paging controller (https://github.com/airbnb/epoxy/pull/586)
- **Fix** Crashes from synchronization in PagedListEpoxyController (https://github.com/airbnb/epoxy/pull/656)
- **Fix** Get onSwipeProgressChanged callbacks on return to original item position (https://github.com/airbnb/epoxy/pull/654)

# 3.1.0 (Dec 4, 2018)
- **Fix** Memory leak in debug mode is removed (https://github.com/airbnb/epoxy/pull/613)
- **Fix** For visibility callbacks, wrong visibility when the view becomes not visible (https://github.com/airbnb/epoxy/pull/619)

# 3.0.0 (Nov 13, 2018)

- **Breaking** Migrated to androidx packages (Big thanks to jeffreydelooff!)

- **Breaking** The `Carousel.Padding` class changed the ordering of its parameters to match Android's ordering of "left, top, right, bottom". (https://github.com/airbnb/epoxy/pull/536 thanks to martinbonnin)
    
   This change won't break compilation, so you _must_ manually change your parameter ordering, otherwise you will get unexpected padding results.

# 2.19.0 (Oct 18, 2018)
This release adds built in support for monitoring visibility of views in the RecyclerView. (https://github.com/airbnb/epoxy/pull/560)

Usage instructions and details are in the wiki - https://github.com/airbnb/epoxy/wiki/Visibility-Events

Huge thanks to Emmanuel Boudrant for contributing this!

# 2.18.0 (Sep 26, 2018)
- **New** A new `PagedListEpoxyController` to improve integration with the Android Paging architecture component (#533 Thanks to Yigit!)
          With this change the old `PagingEpoxyController` has been deprecated, and [the wiki](https://github.com/airbnb/epoxy/wiki/Paging-Support) is updated.

- **New** Add databinding option to not auto apply DoNotHash (#539)
- **Fixed** Fix AsyncEpoxyController constructor to correctly use boolean setting (#537)
- **Fixed** `app_name` is removed from module manifests (#543 Thanks @kettsun0123!)


# 2.17.0 (Sep 6, 2018)
- **New** Add support for setting the Padding via resource or directly in dp (https://github.com/airbnb/epoxy/pull/528 Thanks to pwillmann!)
- **Fixed** Strip kotlin metadata annotation from generated classes (https://github.com/airbnb/epoxy/pull/523)
- **Fixed** Reflect the annotations declared in constructor params (https://github.com/airbnb/epoxy/pull/519 Thanks to Shaishav Gandhi!)

# 2.16.4 (Aug 29, 2018)
- **New** `EpoxyAsyncUtil` and `AsyncEpoxyController` make it easier to use Epoxy's async behavior out of the box
- **New** Epoxy's background diffing posts messages back to the main thread asynchronously so they are not blocked by waiting for vsync

# 2.16.3 (Aug 24, 2018)
- **New** Add `AsyncEpoxyController` for easy access to async support. Change background diffing to post asynchronously to the main thread (https://github.com/airbnb/epoxy/pull/509)

# 2.16.2 (Aug 23, 2018)
- **Fix** Kotlin lambdas can be used in model constructors (https://github.com/airbnb/epoxy/pull/501)
- **New** Added function to check whether a model build is pending (https://github.com/airbnb/epoxy/pull/506)

# 2.16.1 (Aug 15, 2018)
- **Fix** Update EpoxyController async model building so threading works with tests (https://github.com/airbnb/epoxy/pull/504)

# 2.16.0 (Aug 7, 2018)
- **New** EpoxyController now supports asynchronous model building and diffing by allowing you to provide a custom Handler to run these tasks. See the [wiki](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#asynchronous-support) for more details.

- **New** The `EpoxyController#addModelBuildListener` method was added to support listening for when model changes are dispatched to the recyclerview.

# 2.15.0 (July 29, 2018)
- **New** Added kotlin sample code for building models. Updated wiki with info (https://github.com/airbnb/epoxy/wiki/Kotlin-Model-Examples)

- **Fix**  Generated kotlin extension functions now work with Models with type variables (https://github.com/airbnb/epoxy/pull/478)
- **Fix**  Backup is not enabled in manifest now (https://github.com/airbnb/epoxy/pull/481)
- **Fix**  Click listener setter on generated model has correct nullability annotation (https://github.com/airbnb/epoxy/pull/458)
- **Fix**  Avoid kotlin crash using toString on lambdas (https://github.com/airbnb/epoxy/pull/482)
- **Fix**  If EpoxyModelGroup has annotations the generated class now calls super methods correctly.  (https://github.com/airbnb/epoxy/pull/483)

# 2.14.0 (June 27, 2018)
- **New** Experimental support for creating Epoxy models from arbitrary data formats (#450)

# 2.13.0 (June 19, 2018)
- **Fix** Reduce memory usage in model groups and differ (#433)
- **Fix** Support for wildcards in private epoxy attributes (#451)
- **Fix** Generated Kotlin Extensions Don't Adhere to Constructor Nullability (#449)
- **Fix** Infinite loop in annotation processor (#447)

# 2.12.0 (April 18, 2018)

- **Breaking** Several updates to the Paging Library integration were made (https://github.com/airbnb/epoxy/pull/421)
    - The `PagingEpoxyController` class had the methods `setNumPagesToLoad` and `setPageSizeHint` removed
    - Page hints are now taken from the `Config` object off of the PagedList. See the `setConfig` javadoc for information on how config values are used: https://github.com/airbnb/epoxy/blob/master/epoxy-paging/src/main/java/com/airbnb/epoxy/paging/PagingEpoxyController.java#L220
    - Several tweaks were made to how the page size and prefetch distance affect model rebuilding. Take some time to make sure your config values make sense and produce good results for your use case
    - A crash on empty list was fixed (https://github.com/airbnb/epoxy/issues/420)

- **New** The [Paris](https://github.com/airbnb/paris) library is now officially supported to allow dynamically styling RecyclerView items though Epoxy models. See [the wiki](https://github.com/airbnb/epoxy/wiki/Paris-Integration-(Dynamic-Styling)) for more info.


# 2.11.0 (April 7, 2018)

- **Fix** Make databinding work with Android Studio 3.1 (https://github.com/airbnb/epoxy/pull/418)
- Make `EpoxyController#isBuildingModels` public (https://github.com/airbnb/epoxy/pull/406


# 2.10.0 (February 25, 2018)

- **Improved** Allow the `Model_` class suffix for models generated via `@ModelView` to be customized (https://github.com/airbnb/epoxy/pull/402 Big thanks to geralt-encore!)

# 2.9.0 (January 29, 2018)
- **Improved** Global defaults for EpoxyController settings. Set duplicate filtering and exception handlers for all your controllers. (https://github.com/airbnb/epoxy/pull/394)
- **Improved** Add `@NonNull` annotations in EpoxyModel for better Kotlin interop

- **Fixed** Model click listeners now rebind correctly on partial model diffs (https://github.com/airbnb/epoxy/pull/393)
- **Fixed** Update Android Paging library to fix placeholder support (Thanks @wkranich! https://github.com/airbnb/epoxy/pull/360)
- **Fixed** Improve error message for inaccessible private fields (https://github.com/airbnb/epoxy/pull/388)

# 2.8.0 (December 22, 2017)

- **New** Use `@ModelProp` directly on fields to avoid creating a setter (https://github.com/airbnb/epoxy/pull/343)
- **New** Set EpoxyRecyclerView item spacing via xml attribute (https://github.com/airbnb/epoxy/pull/364)
- **New** More flexibility over setting Carousel padding values (https://github.com/airbnb/epoxy/pull/369)
- **New** Allow custom EpoxyModelGroup root view (https://github.com/airbnb/epoxy/pull/370)

- **Fixed** Public visibility settings of the Carousel snap helper settings (https://github.com/airbnb/epoxy/pull/356)
- **Fixed** Add more nullability annotations to better support Kotlin
- **Fixed** Saving view state now works better (https://github.com/airbnb/epoxy/pull/367)

# 2.7.3 (November 21, 2017)

- **Fixed** When a model changed and a partial update was bound to an existing view the wrong values could be set for prop groups (https://github.com/airbnb/epoxy/pull/347)

# 2.7.2 (October 28, 2017)

- **Fixed** Using `EpoxyDataBindingPattern` could result in the wrong package being used for the BR class in generated models.

# 2.7.1 (October 24, 2017)
Several fixes:

- https://github.com/airbnb/epoxy/pull/332
- https://github.com/airbnb/epoxy/pull/329
- https://github.com/airbnb/epoxy/pull/330
- https://github.com/airbnb/epoxy/pull/331

# 2.7.0 (October 17, 2017)

* **New** If a `@ModelView` generated model has a custom base class the generated model will now inherit constructors from the base class (https://github.com/airbnb/epoxy/pull/315)
* **New** Use the `EpoxyDataBindingPattern` annotation to specify a naming pattern for databinding layouts. This removes the need to declare every databinding layout explicitly ([Wiki](https://github.com/airbnb/epoxy/wiki/Data-Binding-Support#automatic-based-on-naming-pattern) - https://github.com/airbnb/epoxy/pull/319)
* **New** If a view with `@ModelView` implements an interface then the generated model will implement a similar interface, enabling polymorphism with models. [Wiki](https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations#view-interfaces)

* **Improvement** `PagingEpoxyController` now has getters to access the underlying data lists (Thanks to @pcqpcq - https://github.com/airbnb/epoxy/pull/317)
* **Improvement** `EpoxyModelGroup` now supports partial rebinds (https://github.com/airbnb/epoxy/pull/316)

# 2.6.0 (October 10, 2017)
* **Improvement** If a `OnModelClickListener` is used it will not be called if a view is clicked while it is being removed or otherwise has no position (https://github.com/airbnb/epoxy/issues/293 - Thanks @niccorder!)

* **New** `EpoxyRecyclerView` and `Carousel` provide out of the box integration with Epoxy along with other enhancements over regular RecyclerView (https://github.com/airbnb/epoxy/wiki/EpoxyRecyclerView)
* **New** `EpoxyPagingController` provides integration with the Android Paging architecture component as well as normal, large lists of items (https://github.com/airbnb/epoxy/wiki/Large-Data-Sets)

#### Kotlin
* **Improvement** Disable kotlin extension function generation with the annotation processor flag `disableEpoxyKotlinExtensionGeneration` (https://github.com/airbnb/epoxy/pull/309)
* **Fix** If a model has a non empty constructor the generated extension function will now use it.


# 2.5.1 (October 2, 2017)
* **Fixed** The wrong import was being generated for models using a view holder in 2.5.0 (https://github.com/airbnb/epoxy/pull/294)
* **Fixed** Fix generated code failing to compile if a subclass of View.OnClickListener is used as an attribute (https://github.com/airbnb/epoxy/pull/296)


# 2.5.0 (September 14, 2017)
* **New Feature** Epoxy now generates a Kotlin DSL to use when building models in your EpoxyController! See [the wiki](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#usage-with-kotlin) for details
* **New Feature** You can use the `autoLayout` parameter in `@ModelView` instead of needing to create a layout resource for `defaultLayout`. Epoxy will then create your view programmatically (https://github.com/airbnb/epoxy/pull/282).

**Breaking**
* The `onSwipeProgressChanged` callback in `EpoxyTouchHelper` had a `Canvas` parameter added (https://github.com/airbnb/epoxy/pull/280). You will need to update any of your usages to add this. Sorry for the inconvenience; this will hopefully help you add better swipe animations.


# 2.4.0 (September 4, 2017)
* **Improvement** If you are setting options on a @ModelProp and have no other annotation parameters you can now omit the explicit `options = ` param name (https://github.com/airbnb/epoxy/pull/268)
* **Improvement** If you are using `@TextProp` you can now specify a default string via a string resource (https://github.com/airbnb/epoxy/pull/269)

* **Fixed** EpoxyModelGroup was not binding model click listeners correctly (https://github.com/airbnb/epoxy/pull/267)
* **Fixed** A model created with @ModelView could fail to compile if it had nullable prop overloads  (https://github.com/airbnb/epoxy/pull/274)

#### Potentially Breaking Fix
A model created with @ModelView with a click listener had the wrong setter name for the model click listener overload (https://github.com/airbnb/epoxy/pull/275)

If you were setting this you will need to update the setter name. If you were setting the click listener to null you may now have to cast it.


# 2.3.0 (August 16, 2017)
* **New** An `AfterPropsSet` annotation for use in `@ModelView` classes. This allows initialization work to be done after all properties are bound from the model. (https://github.com/airbnb/epoxy/pull/242)
* **New** Annotations `TextProp` and `CallbackProp`  as convenient replacements for `ModelProp`. (https://github.com/airbnb/epoxy/pull/260)
* **New** Easy support for dragging and swiping via the `EpoxyTouchHelper` class. https://github.com/airbnb/epoxy/wiki/Touch-Support
* **Change** Added the method `getRootView` to the view holder class in `EpoxyModelGroup` and made the bind methods on `EpoxyModelGroup` non final. This allows access to the root view of the group.
* **Change** Generated models will now inherit class annotations from the base class (https://github.com/airbnb/epoxy/pull/255 Thanks geralt-encore!)

# 2.2.0 (June 19, 2017)
* **Main Feature** Models can now be completely generated from a custom view via annotations on the view. This should completely remove the overhead of creating a model manually in many cases! For more info, see [the wiki](https://github.com/airbnb/epoxy/wiki/Generating-Models-from-View-Annotations)

* **New** Lowered the minimum SDK from 16 to 14.
* **New** Models that have a `View.OnLongClickListener` as an EpoxyAttribute will now have an overloaded setter on the generated model that allows you to set a long click listener that will return the model, view, and adapter position. This is very similar to the `View.OnClickListener` support added in 2.0.0, but for long click listeners. **Upgrade Note** If you were setting a long click listener value to null anywhere you will need to now cast that to `View.OnLongClickListener` because of the new overloaded method.
* **New** `id` overload on EpoxyModel to define a model id with multiple strings
* **New** Option in `EpoxyAttribute` to not include the attribute in the generated `toString` method (Thanks to @geralt-encore!)
* **New** @AutoModel models are now inherited from usages in super classes (Thanks to @geralt-encore!)
* **Fixed** Generated getters could recursively call themselves (Thanks to @geralt-encore!)

# 2.1.0 (May 9, 2017)

* **New**: Support for Android Data Binding! Epoxy will now generate an EpoxyModel directly from a Data Binding xml layout, and handle all data binding details automatically. Thanks to @geralt-encore for helping with this! See more details in [the wiki](https://github.com/airbnb/epoxy/wiki/Data-Binding-Support).
* **New**: Support for Litho. Epoxy will now generate an EpoxyModel for Litho Layout Specs. See more details in [the wiki](https://github.com/airbnb/epoxy/wiki/Litho-Support).
* **New**: Support for implicitly adding AutoModels to an EpoxyController, this let's you drop the extra `.addTo(this)` line. More details and instructions [here](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller#implicit-adding)

# 2.0.0 (March 25, 2017)

* **New**: The `EpoxyController` class helps you manage even models better. This should be used instead of the original `EpoxyAdapter` in most places. Read more about `EpoxyController` in [the wiki](https://github.com/airbnb/epoxy/wiki/Epoxy-Controller).
* **Change**: In the new EpoxyController, the diffing algorithm uses both `equals` and `hashCode` on each model to check for changes. This is a change from the EpoxyAdapter where only `hashCode` was used. Generated models have both hashCode and equals implemented properly already, but if you have any custom hashCode implementations in your models make sure you have equals implemented as well.
* **New**: Models that have a `View.OnClickListener` as an EpoxyAttribute will now have an overloaded setter on the generated model that allows you to set a click listener that will return the model, view, and adapter position. **Upgrade Note** If you were setting a click listener value to null anywhere you will need to now cast that to `View.OnClickListener` because of the new overloaded method.
* **New**: Attach an onBind/onUnbind listener directly to a model instead of overriding the onModelBound method. Generated models will have methods created to set this listener and handle the callback for you.
* **New**: Support for creating models in Kotlin (Thanks to @geralt-encore! https://github.com/airbnb/epoxy/pull/144)
* **New**: `EpoxyModelWithView` supports creating a View programmatically instead of inflating from XML.
* **New**: `EpoxyModelGroup` supports grouping models together in arbitrary formations.
* **New**: Instead of setting attribute options like `@EpoxyAttribute(hash = false)` you should now do `@EpoxyAttribute(DoNotHash)`. You can also set other options like that.
* **New**: Annotation processor options can now be set via gradle instead of with `PackageEpoxyConfig`
* **New**: In an EpoxyController, if a model with the same id changes state Epoxy will include its previous state as a payload in the change notification. The new model will have its `bind(view, previouslyBoundModel)` method called so it can compare what changed since the previous model, and so it can update the view with only the data that changed.

# 1.7.5 (Feb 21, 2017)

* **New**: Models inherit layouts specified in superclass `@EpoxyModelClass` annotations [#119](https://github.com/airbnb/epoxy/pull/119)
* **New**: Support module configuration options [#124](https://github.com/airbnb/epoxy/pull/124)

# 1.6.2 (Feb 8, 2017)

* New: Support layout resource annotations in library projects (https://github.com/airbnb/epoxy/pull/116)

# 1.6.1 (Feb 6, 2017)

* Allow the default layout resource to be specified in the EpoxyModelClass class annotation [(#109)](https://github.com/airbnb/epoxy/pull/109) [(#111)](https://github.com/airbnb/epoxy/pull/111)
* Allow the `createNewHolder` method to be omitted and generated automatically [(#105)](https://github.com/airbnb/epoxy/pull/105)
* Generate a subclass for abstract model classes if the EpoxyModelClass annotation is present [(#105)](https://github.com/airbnb/epoxy/pull/105)
* Allow strings as model ids [(#107)](https://github.com/airbnb/epoxy/pull/107)
* Add instructions to readme for avoiding memory leaks [(#106)](https://github.com/airbnb/epoxy/pull/106)
* Add model callbacks for view attached/detached from window, and onFailedToRecycleView [(#104)](https://github.com/airbnb/epoxy/pull/104)
* Improve documentation on model unbind behavior [(#103)](https://github.com/airbnb/epoxy/pull/103)
* Fix generated methods from super classes that have var args [(#100)](https://github.com/airbnb/epoxy/pull/100)
* Remove apt dependency [(#95)](https://github.com/airbnb/epoxy/pull/95)
* Add `removeAllModels` method to EpoxyAdapter [(#94)](https://github.com/airbnb/epoxy/pull/94)
* Use actual param names when generating methods from super classes [(#85)](https://github.com/airbnb/epoxy/pull/85)

# 1.5.0 (11/21/2016)

* Fixes models being used in separate modules
* Generates a `reset()` method on each model to reset annotated fields to their defaults.
* Changes `@EpoxyAttribute(hash = false)` to still differentiate between null and non null values in the hashcode implementation
* Adds a `notifyModelChanged` method to EpoxyAdapter that allows a payload to be specified
* Generates a `toString()` method on all generated model classes that includes the values of all annotated fields.

# 1.4.0 (10/13/2016)

* Optimizations to the diffing algorithm
* Setters on generated classes are not created if an @EpoxyAttribute field is marked as `final`
* Adds @EpoxyModelClass annotation to force a model to have a generated class, even if it doesn't have any @EpoxyAttribute fields
* Fix to not generate methods for package private @EpoxyAttribute fields that are in a different package from the generated class
* Have generated classes duplicate any super methods that have the model as the return type to help with chaining

# 1.3.0 (09/15/2016)

* Add support for using the view holder pattern with models. See the readme for more information.
* Throw an exception if `EpoxyAdapter#notifyDataSetChanged()` is called when diffing is enabled. It doesn't make sense to allow this alongside diffing, and calling this is most likely to be an accidental mixup with `notifyModelsChanged()`.
* Some performance improvements with the diffing algorithm.

# 1.2.0 (09/07/2016)

* Change signature of `EpoxyAdapter#onModelBound` to include the model position
* Fix EpoxyModel hashcode to include the layout specified by `getDefaultLayout`
* Enforce that the id of an `EpoxyModel` cannot change once it has been added to the adapter
* Add optional hash parameter to the `EpoxyAttribute` annotation to exclude a field from being included in the generated hashcode method.

# 1.1.0 (08/24/2016)

* Initial release
