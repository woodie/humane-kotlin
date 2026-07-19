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
`gorderly`-equivalent needed here, unlike Go. `ProjectConfig.kt` pins
spec/test execution order so full-suite output is reproducible, matching
`next-caltrain-kotlin`'s own `ProjectConfig`. `build.gradle.kts`'s custom
`TestListener` (copied from `next-caltrain-kotlin`'s `app/build.gradle.kts`)
prints that tree as a dense RSpec-`-fd`-style block -- see that file's own
comments for the full reasoning.

## Current status

Freshly scaffolded, not yet built or run anywhere -- the Cowork sandbox has
no Kotlin/JDK 17+ toolchain (only JDK 11), so `Humane.kt` and the three spec
files were written by inspection against the established Go/Ruby/Swift
ports and Kotlin's real language semantics, not verified by compiling. First
things to do on a real machine:

1. `make build` -- confirms `Humane.kt` actually compiles (Kotlin range/`when`
   syntax, `Instant` arithmetic, and the `formatSignificant`/`Locale.ROOT`
   formatting are the riskiest bits to have gotten subtly wrong sight-unseen).
2. `make test` -- confirms every fixture ported from the other three
   languages produces the same output here.
3. `make lint` -- `ktlint` hasn't been run against this codebase yet at all.

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
