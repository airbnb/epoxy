Releasing
========

 1. Bump the VERSION_NAME property in `gradle.properties` based on Major.Minor.Patch naming scheme
 2. Update `CHANGELOG.md` for the impending release.
 3. Update the `README.md` with the new version.
 4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the version you set in step 1)
 5. `git tag -a X.Y.X -m "Version X.Y.Z"` (where X.Y.Z is the new version)
 6. `./gradlew clean uploadArchives`
 9. `git push && git push --tags`
 10. Visit [Sonatype Nexus](https://oss.sonatype.org/) and promote the artifact.