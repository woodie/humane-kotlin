# humane-kotlin

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

JDK 17+ (see `docs/COWORK.md` for why). Kotlin 2.2.10, matching
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
Gradle composite build (`includeBuild`), not a published artifact. See
`huck`'s own `docs/COWORK.md`.
