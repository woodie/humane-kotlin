plugins {
    kotlin("jvm") version "2.2.10"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("com.netpress.kotidy")
}

group = "com.netpress"
version = "0.1.0"

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
