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

    // Custom RSpec/ginkgo-fd-style console reporter -- copied from next-caltrain-kotlin's
    // app/build.gradle.kts (see that file's own comments for the full reasoning). Hooks
    // Gradle's TestListener API directly to print a dense nested tree from Kotest's flat
    // per-leaf-test callbacks, with no blank-line padding and no extra tool/binary needed --
    // Kotest's own DescribeSpec/Gradle's TestDescriptor.parent chain already carry the real
    // describe/context/it hierarchy, unlike Go's flat `go test -v` output.
    var lastPath: List<String> = emptyList()

    val colorEnabled = System.getenv("NO_COLOR") == null
    val reset = "[0m"
    val green = "[32m"
    val red = "[31m"
    val cyan = "[36m"
    val gray = "[90m"

    fun ansi(
        code: String,
        text: String,
    ) = if (colorEnabled) "$code$text$reset" else text

    fun ancestry(descriptor: TestDescriptor): List<String> {
        val names = mutableListOf<String>()
        var d = descriptor.parent
        while (d != null) {
            if (!d.name.startsWith("Gradle Test")) names.add(0, d.name)
            d = d.parent
        }
        return names
    }

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

                val shared = path.zip(lastPath).takeWhile { (a, b) -> a == b }.count()
                for (depth in shared until ancestors.size) {
                    if (depth == 0) println()
                    println("  ".repeat(depth) + ancestors[depth])
                }

                val line =
                    when (result.resultType) {
                        TestResult.ResultType.SUCCESS ->
                            "${ansi(green, "✔")} ${ansi(gray, testDescriptor.name)}"

                        TestResult.ResultType.SKIPPED ->
                            ansi(cyan, "○ ${testDescriptor.name}")

                        else ->
                            ansi(red, "✖ ${testDescriptor.name}")
                    }
                println("  ".repeat(ancestors.size) + line)
                if (result.resultType == TestResult.ResultType.FAILURE) {
                    result.exceptions.forEach { e ->
                        println("  ".repeat(ancestors.size + 1) + ansi(red, e.message ?: e.toString()))
                    }
                }

                lastPath = path
            }
        },
    )
}
