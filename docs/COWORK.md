# Picking up humane-kotlin in a new Cowork session

Context for whoever opens this repo cold, with none of the prior conversation
history. Cross-project conventions (git locks, sandbox toolchain gaps,
pushing, comments, code style) are in `~/workspace/woodie/docs/COWORK.md`.

## What this is

A small Kotlin/JVM library formatting file sizes and relative dates the way
macOS Finder does -- the fourth-language sibling of
[`humane`](https://github.com/woodie/humane) (Go),
[`humane-ruby`](https://github.com/woodie/humane-ruby), and
[`humane-swift`](https://github.com/woodie/humane-swift). Same algorithm,
same wording, same fixtures; see each sibling's own `docs/COMMENTS.md` for
the fixture history behind the specific rounding/wording rules `Humane.kt`
ports. `docs/COMMENTS.md` here covers Kotlin-specific decisions (why `Long`
not `Int`, why `timeAgo`'s `at` is non-nullable unlike the Ruby/Swift
siblings, etc.).

Built specifically as [`huck`](https://github.com/woodie/huck)'s size/time
formatting dependency -- see "Consumed by" below.

## Writing tests here

Tests use [Kotest](https://kotest.io)'s `DescribeSpec` -- the same
`describe`/`context`/`it` shape as every other language in this account,
and the same convention `next-caltrain-kotlin` already established for
Kotlin specifically (see its `GoodTimesSpec.kt`). Shape:

```kotlin
class HumanSizeSpec : DescribeSpec({
    describe("Humane.humanSize") {
        var bytes = 0L
        val subject = { Humane.humanSize(bytes) }

        context("with 0 bytes") {
            beforeEach { bytes = 0 }

            it("formats as Zero KB") {
                subject() shouldBe "Zero KB"
            }
        }
    }
})
```

One `subject` closure declared once per `describe`; each `context` sets the
shared var it reads via `beforeEach` rather than a context-body `val`
computed once at tree construction -- `beforeEach` re-runs per `it`, matching
RSpec's real `let` laziness. Same shape as `humane`'s Go `size_test.go`/
`time_test.go` and `humane-swift`'s specs; see `docs/COMMENTS.md`'s "Test
structure" entry and `~/workspace/woodie/docs/COWORK.md`'s "Test structure"
section for why.

Kotest's `DescribeSpec` renders the real nested tree natively -- no
CLI-wrapper-style tool needed here, unlike Go's `gorderly` or Swift's
`xctidy`. `ProjectConfig.kt` pins spec/test execution order so full-suite
output is reproducible, matching `next-caltrain-kotlin`'s own
`ProjectConfig`. Printing that tree as a dense block used to be a
byte-for-byte-copied `TestListener` block directly in `build.gradle.kts`
(also mirrored into `next-caltrain-kotlin`/`huck`); it's now
[`kotidy`](https://github.com/woodie/kotidy), a real Gradle plugin the
three repos share instead of hand-syncing. Originally consumed as a
composite build; `com.netpress.kotidy` is now approved and live on the
Gradle Plugin Portal, so `build.gradle.kts` just pins
`id("com.netpress.kotidy") version "0.1.0"` + `kotidy { style = "fs" }`,
resolved via the `gradlePluginPortal()` already in
`settings.gradle.kts`'s `pluginManagement.repositories` -- no sibling
checkout of `kotidy` needed on disk or in CI anymore (`.github/workflows/CI.yml`
dropped its old sibling-checkout step). See `kotidy`'s own `docs/COWORK.md` for the extraction
history and its README for the full style table (the old block's comments
called this "RSpec-`-fd`-style," which was never quite accurate --
checkmark-plus-gray-name is `kotidy`'s `fs`/Mocha-spec style; `fd` is the
real no-glyph RSpec doc format).

## Current status

`make build` and `make test` both confirmed green on a real Mac (JDK 17
auto-downloaded via the `foojay-resolver-convention` plugin, added after the
first `make build` failed looking for a JDK 17 that wasn't installed -- see
git history). All 42 examples across `HumanSizeSpec`/`DistanceInTimeSpec`/
`TimeAgoSpec` pass, including every boundary-table cutoff
(44:29/44:30, 89:29/89:30, 23:59:29/23:59:30) -- the port matches
`humane`/`humane-ruby`/`humane-swift`'s fixtures exactly. The custom
`TestListener` tree renders correctly too, confirmed by real console output.

`make lint` (`ktlintCheck`) hasn't been run against this codebase yet --
worth doing before considering this fully done, since `huck`'s equivalent
check did catch a real style violation on its first run.

## Requirements

JDK 17+ (not JDK 11 -- this matters because `huck` needs 17+ for `jpackage`,
and since `huck` consumes this via a Gradle composite build rather than a
published artifact, both need compatible bytecode). Kotlin `2.2.10`, Kotest
`5.9.1` -- matching `next-caltrain-kotlin`'s pinned versions for consistency,
not because either is required to.

## Consumed by

[`huck`](https://github.com/woodie/huck) -- a Windows desktop client (Kotlin/
Compose Multiplatform port of [`zouk`](https://github.com/woodie/zouk)'s scan
client) -- via `huck`'s `settings.gradle.kts` doing
`includeBuild("../humane-kotlin")`. That means both repos need to sit as
sibling directories on disk (`~/workspace/humane-kotlin`,
`~/workspace/huck`) for `huck`'s build to resolve this dependency at all --
there's no published artifact to fall back on. See `huck`'s own
`docs/COWORK.md` for the full reasoning behind that choice over publishing
to Maven Central/GitHub Packages.
