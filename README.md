# humane-kotlin

[![Kotlin](https://img.shields.io/badge/dynamic/toml?url=https%3A%2F%2Fraw.githubusercontent.com%2Fwoodie%2Fnext-caltrain-kotlin%2Fmain%2Fgradle%2Flibs.versions.toml&query=%24.versions.kotlin&label=Kotlin&color=7F52FF&logo=kotlin&logoColor=white)](gradle/libs.versions.toml)
[![CI](https://github.com/woodie/next-caltrain-kotlin/actions/workflows/CI.yml/badge.svg)](https://github.com/woodie/next-caltrain-kotlin/actions/workflows/CI.yml)
[![Release](https://img.shields.io/github/v/release/woodie/next-caltrain-kotlin.svg)](https://github.com/woodie/next-caltrain-kotlin/releases/latest)
[![License](https://img.shields.io/github/license/woodie/next-caltrain-kotlin.svg)](LICENSE)

Swift's file sizes and relative dates for Kotlin.

A small JVM library formatting file sizes and relative dates the way macOS
Finder does, matching [`humane`](https://github.com/woodie/humane) (Go),
[`humane-ruby`](https://github.com/woodie/humane-ruby), and
[`humane-swift`](https://github.com/woodie/humane-swift) -- same algorithm,
same wording, same fixtures, ported to Kotlin's own idiom.

```kotlin
import com.netpress.humane.Humane

Humane.humanSize(225_935)                          // "226 KB"
Humane.distanceInTime(at, Instant.now())           // "3 minutes ago"
Humane.timeAgo(downloadedAt)                       // "3 minutes ago"
```

## Requirements

JDK 17+ (not JDK 11 -- `huck` needs 17+ for `jpackage`, and since it
consumes this library via a Gradle composite build rather than a published
artifact, both need compatible bytecode). Kotlin 2.2.10, matching
[`next-caltrain-kotlin`](https://github.com/woodie/next-caltrain-kotlin)'s
pinned version.

## Development

```
make build   # ./gradlew build -x test
make test    # ./gradlew test -- Kotest's real nested describe/context/it output
make lint    # ./gradlew ktlintCheck
make check   # ./gradlew check
```

## Consumed by

[`huck`](https://github.com/woodie/huck) -- a Windows desktop client, via a
Gradle composite build (`includeBuild("../humane-kotlin")`), not a
published artifact, so this repo has to exist as a sibling directory on
disk for `huck` to build -- see `huck`'s own README, "Requirements".
