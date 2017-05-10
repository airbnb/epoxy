# Epoxy

Epoxy is an Android library for building complex screens in a RecyclerView. It abstracts the boilerplate of view holders, item types, item ids, span counts, and more, in order to simplify building screens with multiple view types. Additionally, Epoxy adds support for saving view state and automatic diffing of item changes.

[We developed Epoxy at Airbnb](https://medium.com/airbnb-engineering/epoxy-airbnbs-view-architecture-on-android-c3e1af150394#.xv4ymrtmk) to simplify the process of working with RecyclerViews, and to add the missing functionality we needed. We now use Epoxy for most of the main screens in our app and it has improved our developer experience greatly.

* [Installation](#installation)
* [Documentation](#documentation)
* [Contributing](#contributing)
* [Sample App](/epoxy-sample)

## Installation

Gradle is the only supported build configuration, so just add the dependency to your project `build.gradle` file:

```groovy
dependencies {
  compile 'com.airbnb.android:epoxy:2.1.0'
}
```

Optionally, if you want to [take advantage of generated models](https://github.com/airbnb/epoxy/wiki/Epoxy-Models#annotations) you must also provide the annotation processor as a dependency.
```groovy
dependencies {
  compile 'com.airbnb.android:epoxy:2.1.0'
  annotationProcessor 'com.airbnb.android:epoxy-processor:2.1.0'
}
```

## Documentation
See examples and browse complete documentation at the [Epoxy Wiki](https://github.com/airbnb/epoxy/wiki)

If you still have questions, feel free to create a new issue.

## Contributing
Pull requests are welcome! We'd love help improving this library. Feel free to browse through open issues to look for things that need work. If you have a feature request or bug, please open a new issue so we can track it.

## License

```
Copyright 2016 Airbnb, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
