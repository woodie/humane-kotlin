# humane-kotlin

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?logo=kotlin&logoColor=white)](build.gradle.kts)
[![CI](https://github.com/woodie/humane-kotlin/actions/workflows/CI.yml/badge.svg)](https://github.com/woodie/humane-kotlin/actions/workflows/CI.yml)
[![Release](https://img.shields.io/github/v/release/woodie/humane-kotlin.svg)](https://github.com/woodie/humane-kotlin/releases/latest)
[![License](https://img.shields.io/github/license/woodie/humane-kotlin.svg)](LICENSE)

File sizes that read exactly the way macOS Finder shows them (`"4.1 MB"`,
`"226 KB"`), and relative dates in Rails ActionView's wording (`"about 3 hours
ago"`, `"in 3 minutes"`), for Kotlin and the JVM.

```kotlin
import com.netpress.humane.Humane

Humane.humanSize(225_935) // "226 KB"

val mtime = Instant.now().minusSeconds(180)
Humane.timeAgo(mtime) // "3 minutes ago" -- relative to the real clock

val now = Instant.now()
Humane.distanceInTime(mtime, now) // "3 minutes ago" -- explicit relativeTo, for tests
```

## Install

Published to Maven Central. With `mavenCentral()` in your repositories:

```kotlin
dependencies {
    implementation("com.netpress:humane-kotlin:0.1.2")
}
```

Requires JDK 17 or later.

## File sizes

`Humane.humanSize` matches Foundation's `ByteCountFormatter` (file style),
which is what macOS Finder shows, and is checked against real Finder output.
The JVM has no equivalent formatter, so the rules are implemented directly:

- 1000-based units with capitalized labels: `KB`, `MB`, `GB`, `TB`, and up.
- A fixed number of decimal places per unit -- none for KB, one for MB, two
  for GB and above -- with trailing zeros dropped (`"2 MB"`, not `"2.0 MB"`).
- Rounds half up, and moves up a unit when rounding reaches 1000:
  999,999 bytes is `"1 MB"`, not `"1000 KB"`.
- Zero bytes is `"Zero KB"`, and anything under 1000 bytes is spelled out
  (`"1 byte"`, `"7 bytes"`).
- Always uses a period for the decimal point, whatever the JVM's locale.

```kotlin
Humane.humanSize(2_579)         // "3 KB"
Humane.humanSize(4_132_383)     // "4.1 MB"
Humane.humanSize(999_999)       // "1 MB"
Humane.humanSize(5_240_000_000) // "5.24 GB"
```

`humanSize` takes a `Long`, since gigabyte-scale sizes overflow an `Int`.

## `distanceInTime` and `timeAgo`

Two entry points, same naming split as ActionView's own
`distance_of_time_in_words`/`time_ago_in_words`:

- **`distanceInTime(at: Instant?, relativeTo: Instant, ...)`** -- the
  explicit, fully-tested core. Takes both times, plus a possibly-null `at`,
  so it's what tests should call.
- **`timeAgo(at: Instant, ...)`** -- a one-argument convenience for the
  common "drop into a view" case, where a timestamp is always in hand.
  Supplies `Instant.now()` as `relativeTo` internally.

The wording follows ActionView's `distance_of_time_in_words`, with one
difference: direction is picked for you. A past time reads `"3 minutes ago"`
and a future one `"in 3 minutes"`, where ActionView leaves that to the caller.

Both share the same named arguments and recommended defaults, already
matching ActionView's own `distance_of_time_in_words` defaults -- pass none
at all and you get them for free:

```kotlin
Humane.distanceInTime(at, relativeTo) // approximate = true, includeSeconds = false
Humane.timeAgo(at)                    // same defaults, relativeTo is Instant.now()
```

- **`approximate`** (default `true`): prefixes `"about"`/`"in about"` on the
  hour-scale buckets (1 hour, and 2..24 hours), matching ActionView's
  `distance_of_time_in_words` wording for those buckets exactly (down to its
  44:30/89:30 rounding cutoffs), through the "1 day" bucket.
- **`includeSeconds`** (default `false`): under 30 seconds, collapses to
  `"less than a minute ago"`/`"in less than a minute"` instead of an exact
  second count. Matches ActionView's `include_seconds` default.
- **`whenNil`** (default `""`): if `at` is `null`, `distanceInTime` returns
  this string without formatting -- for a scan, download, or other record
  that doesn't have a timestamp yet. `timeAgo` takes a non-null `at`; use
  `distanceInTime` directly for a possibly-missing timestamp.

```kotlin
Humane.distanceInTime(at, relativeTo, approximate = false) // "15 hours ago", not "about 15 hours ago"
Humane.distanceInTime(null, Instant.now(), whenNil = "an unknown time") // "an unknown time"
```

## Scope

Finder's byte-count style, and a numeric (non-calendar-aware) relative time
style through the "1 day" bucket -- that's the whole surface area today.
Alternate size units/styles and a named style (`"yesterday"`,
calendar-boundary-aware) aren't implemented -- contributions welcome.

## Development

```
make test
make check
```

`make test` prints the full nested describe/context/it output; `make check`
runs lint and tests together, quiet unless something fails. Both run
`ktlintFormat` first. `make lint` checks style without fixing anything.
