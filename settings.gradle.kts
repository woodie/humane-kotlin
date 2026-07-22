pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    // No published artifact for kotidy yet -- this is a Gradle composite
    // build, not a version dependency, and specifically needs to be included
    // from inside pluginManagement (not the main settings.gradle.kts body,
    // unlike humane-kotlin's own consumer huck's plain includeBuild for a
    // regular library dependency) since it supplies a plugin ID rather than
    // a library. Requires kotidy checked out as a sibling directory
    // (../kotidy relative to this file). See its own docs/COWORK.md.
    includeBuild("../kotidy")
}

plugins {
    // Lets Gradle auto-download a matching JDK when jvmToolchain(17) (see
    // build.gradle.kts) can't find one already installed, instead of failing
    // outright -- matches next-caltrain-kotlin's own settings.gradle.kts.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "humane-kotlin"
