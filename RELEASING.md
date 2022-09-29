Releasing
========

1. Bump the VERSION_NAME property in `gradle.properties` based on Major.Minor.Patch naming scheme
2. Update `CHANGELOG.md` for the impending release.
3. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the version you set in step 1)
4. Add your sonatype login information under gradle properties mavenCentralUsername and mavenCentralPassword in your local user gradle.properties file
5. `./gradlew publish` to build the artifacts and publish them to maven
7. Open PR on Github, merge, and publish release through Github UI.