# Picking up humane-kotlin in a new Cowork session

Cross-project conventions (git locks, sandbox toolchain gaps, pushing, comments, code
style) are in `~/workspace/woodie/docs/COWORK.md`.

## What this is

The fourth-language sibling of `humane` (Go), `humane-ruby`, and `humane-swift` --
same algorithm, same wording, same fixtures. `object Humane` namespaces `humanSize`,
`distanceInTime`, and `timeAgo` (a one-argument convenience over `distanceInTime`
supplying `Instant.now()`). Built specifically as `huck`'s size/time formatting
dependency. See `docs/COMMENTS.md` for Kotlin-specific decisions (why `Long` not
`Int`, why `timeAgo`'s `at` is non-nullable unlike the Ruby/Swift siblings, etc.).

## Testing

Kotest `DescribeSpec`, `justBeforeEach` over inline setup (matches `humane`'s Go
suite and the rest of the family). `ProjectConfig.kt` pins spec/test execution order
for reproducible full-suite output.

`build`/`test`/`check` Makefile targets all run `ktlintFormat` before Gradle's own
task, so lint issues surface (and mechanical ones auto-fix) on every normal build --
no separate lint pass needs remembering.

## Sandbox limitation

No JDK/network route to Maven Central from the sandbox -- changes are written by
inspection, verified via `make build`/`make test` on the user's own Mac (JDK 17+).
Publishing never runs locally either: pushing a `v*` tag runs `CI.yml`'s
`publish` job from repo secrets.

## Current status

`v0.1.2`, **published to Maven Central** as `com.netpress:humane-kotlin` (`humane`
issue #3's rounding fix) -- the first release published by `CI.yml`'s tag-triggered
`publish` job, after re-setting the signing secrets to the current key (see
`woodie/docs/COWORK.md`, "Maven Central signing"). Consumed by `huck` via a plain
`implementation("com.netpress:humane-kotlin:0.1.1")` until its pin is bumped -- no
sibling checkout or composite build (`includeBuild`) needed anymore; that was true
through `v0.1.0` only.
