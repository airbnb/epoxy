Releasing
========

 1. Bump the VERSION_NAME property in `gradle.properties` based on Major.Minor.Patch naming scheme
 2. Update `CHANGELOG.md` for the impending release.
 3. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the version you set in step 1)
 4. `./gradlew clean uploadArchives --no-daemon --no-parallel`
 5. Visit [Sonatype Nexus](https://oss.sonatype.org/) and promote the artifact.
 6. Open PR with on Github, merge, and publish release through Github UI.