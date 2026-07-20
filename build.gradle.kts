import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult

plugins {
    kotlin("jvm") version "2.2.10"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
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

    // Custom RSpec/ginkgo-fd-style console reporter -- copied byte-for-byte from
    // next-caltrain-kotlin's app/build.gradle.kts (also mirrored into huck; see that
    // file's own comments for the full reasoning). Hooks Gradle's TestListener API
    // directly to print a dense nested tree from Kotest's flat per-leaf-test
    // callbacks, with no blank-line padding and no extra tool/binary needed --
    // Kotest's own DescribeSpec/Gradle's TestDescriptor.parent chain already carry the
    // real describe/context/it hierarchy, unlike Go's flat `go test -v` output.
    //
    // Kept identical across all three repos on purpose, right down to the
    // SCREAMING_SNAKE_CASE constant names below (see .editorconfig's
    // ktlint_standard_property-naming disable, which is what stops ktlintFormat from
    // silently lowercasing them back to reset/green/red/cyan/gray on every run --
    // this repo had no .editorconfig at all until that was added, which is the actual
    // reason this had drifted from caltrain's copy).
    var lastPath: List<String> = emptyList()

    // Respect the NO_COLOR convention (https://no-color.org/) for anyone piping
    // this into a log file or a terminal that mangles escape codes.
    val colorEnabled = System.getenv("NO_COLOR") == null
    val RESET = "[0m"
    val GREEN = "[32m"
    val RED = "[31m"
    val CYAN = "[36m"
    val GRAY = "[90m"

    fun ansi(
        code: String,
        text: String,
    ) = if (colorEnabled) "$code$text$RESET" else text

    fun ancestry(descriptor: TestDescriptor): List<String> {
        val names = mutableListOf<String>()
        var d = descriptor.parent
        while (d != null) {
            if (!d.name.startsWith("Gradle Test")) names.add(0, d.name)
            d = d.parent
        }
        return names
    }

    // Reset dedupe state at actual task-execution time, not here at configuration
    // time. doFirst always re-runs on every invocation regardless of Gradle's
    // configuration cache, so this is the one safe place to reset from -- matches
    // caltrain's own comment on this same line.
    doFirst {
        lastPath = emptyList()
    }

    addTestListener(
        object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}

            override fun afterSuite(
                suite: TestDescriptor,
                result: TestResult,
            ) {}

            override fun beforeTest(testDescriptor: TestDescriptor) {}

            override fun afterTest(
                testDescriptor: TestDescriptor,
                result: TestResult,
            ) {
                val ancestors = ancestry(testDescriptor)
                val path = ancestors + testDescriptor.name

                // Print only the part of the path not already printed for the previous
                // test -- the "dedupe shared prefix" trick that produces a real nested
                // tree from a flat stream of leaf-test callbacks, with no blank lines.
                val shared = path.zip(lastPath).takeWhile { (a, b) -> a == b }.count()
                for (depth in shared until ancestors.size) {
                    // depth == 0 means ancestors[0] -- the fully-qualified spec class
                    // name -- is about to be printed for a new top-level suite. A blank
                    // line goes before every one of those, unconditionally, so each
                    // suite's block visually stands apart from whatever came before it.
                    if (depth == 0) println()
                    println("  ".repeat(depth) + ancestors[depth])
                }

                // Mocha's own spec reporter colors the checkmark green and dims the title
                // for passes; failures and pending get a single solid color instead.
                val line =
                    when (result.resultType) {
                        TestResult.ResultType.SUCCESS ->
                            "${ansi(GREEN, "✔")} ${ansi(GRAY, testDescriptor.name)}"

                        TestResult.ResultType.SKIPPED ->
                            ansi(CYAN, "○ ${testDescriptor.name}")

                        else ->
                            ansi(RED, "✖ ${testDescriptor.name}")
                    }
                println("  ".repeat(ancestors.size) + line)
                if (result.resultType == TestResult.ResultType.FAILURE) {
                    result.exceptions.forEach { e ->
                        println("  ".repeat(ancestors.size + 1) + ansi(RED, e.message ?: e.toString()))
                    }
                }

                lastPath = path
            }
        },
    )
}
