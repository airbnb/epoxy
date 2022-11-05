Releasing
========

1. Bump the VERSION_NAME property in `gradle.properties` based on Major.Minor.Patch naming scheme
2. Update `CHANGELOG.md` for the impending release.
3. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the version you set in step 1)
4. Add your sonatype login information under gradle properties mavenCentralUsername and mavenCentralPassword in your local user gradle.properties file
5. Make sure you have a gpg signing key configured (https://vanniktech.github.io/gradle-maven-publish-plugin/central/#secrets)
5. `./gradlew publish` to build the artifacts and publish them to maven
7. Open PR on Github, merge, and publish release through Github UI.

Publishing a release to an internal repository
========

To publish an internal release to an Artifactory repository:

1. Set credential values for ARTIFACTORY_USERNAME and ARTIFACTORY_PASSWORD in your local gradle.properties
2. Set values for ARTIFACTORY_RELEASE_URL (and optionally ARTIFACTORY_SNAPSHOT_URL if you are publishing a snapshot)
3. /gradlew publishAllPublicationsToAirbnbArtifactoryRepository -PdoNotSignRelease=true
4. "-PdoNotSignRelease=true" is optional, but we don't need to sign artifactory releases and this allows everyone to publish without setting up a gpg key

If you need to publish to a different repository, look at the configuration in 'publishing.gradle'
to see how to configure additional repositories.

Maven Local Installation
=======================

If testing changes locally, you can install to mavenLocal via `./gradlew publishToMavenLocal`