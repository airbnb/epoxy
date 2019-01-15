# Epoxy is an Open Source Project

Pull requests are welcome! We'd love help improving this library.

We have a code style setting for the project (checkstyle for Java, ktlint for Kotlin). Please run `Reformat Code` in Android Studio (or Intellij) on changed files before pushing them. Alternatively for Kotlin you can use [ktlint](https://ktlint.github.io/) tasks: check Kotlin code formatting with`./gradlew ktlint` and reformat all Kotlin code with `./gradlew ktlintformat`.

Also, run `./gradlew check` locally to make sure that style checks and tests pass.

If you update the model annotation processor you may find the `update_processor_test_resources.rb` script very useful for updating the existing tests with your changes.