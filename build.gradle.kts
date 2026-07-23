plugins {
    kotlin("jvm") version "2.2.10"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("com.netpress.kotidy") version "0.1.0"
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "com.netpress"
version = "0.1.1"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Replaces the custom RSpec/ginkgo-fd-style TestListener that used to live
// directly in this file (copy-pasted byte-for-byte from next-caltrain-kotlin,
// also mirrored into huck) -- see kotidy's own docs/COWORK.md for why it was
// extracted into a real plugin instead of staying a hand-synced block, and
// settings.gradle.kts's includeBuild comment for the composite-build
// mechanism. "fs" is the closest existing style to what this project's
// output looked like before (checkmark + gray name for passes) -- not
// byte-identical, since the old ad hoc block's fail/skip glyphs didn't
// actually match any single named style; see kotidy's README for the real
// Mocha-spec-format shape this now renders instead.
kotidy {
    style = "fs"
}

// Maven Central (Central Publishing Portal) release config -- see issue #1.
// GPG signing credentials come from ~/.gradle/gradle.properties locally, or
// ORG_GRADLE_PROJECT_-prefixed env vars in CI; never checked in here.
// publishToMavenCentral() with no args is manual publish (deployment lands
// in "Validated" state on the Portal for a manual click), matching the
// issue's call for the first release -- switch to
// publishToMavenCentral(true) later if automatic release is wanted.
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates("com.netpress", "humane-kotlin", version.toString())

    pom {
        name.set("humane-kotlin")
        description.set(
            "Humane file size and relative date formatting, the way macOS Finder does it.",
        )
        url.set("https://github.com/woodie/humane-kotlin")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/woodie/humane-kotlin/blob/main/LICENSE")
            }
        }
        developers {
            developer {
                id.set("woodie")
                name.set("John Woodell")
                email.set("woodie@netpress.com")
            }
        }
        scm {
            url.set("https://github.com/woodie/humane-kotlin")
            connection.set("scm:git:https://github.com/woodie/humane-kotlin.git")
            developerConnection.set("scm:git:ssh://git@github.com/woodie/humane-kotlin.git")
        }
    }
}
