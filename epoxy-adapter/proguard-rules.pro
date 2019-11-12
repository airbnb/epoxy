# The generated ControllerHelper classes are needed when using AutoModel annotations.
# Each ControllerHelper is looked up reflectively, so we need to make sure it is
# kept and its name not obfuscated so the reflective lookup works.
-keep class * extends com.airbnb.epoxy.EpoxyController { *; }
-keep class * extends com.airbnb.epoxy.ControllerHelper { *; }
-keepclasseswithmembernames class * { @com.airbnb.epoxy.AutoModel <fields>; }