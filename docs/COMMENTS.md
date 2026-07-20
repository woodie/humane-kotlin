# Comments

Rationale, history, and design notes that don't belong as multi-line comments
in the source. Organized by file, then by the type, property, or function
each note is attached to. See `humane`/`humane-ruby`/`humane-swift`'s own
`docs/COMMENTS.md` for the pattern this follows.

## build.gradle.kts

### `.editorconfig`'s `ktlint_standard_property-naming` disable
This repo had no `.editorconfig` at all until huck's `TestListener` reporter
was traced to be silently drifting from `next-caltrain-kotlin`'s original:
without a disable for ktlint's `standard:property-naming` rule, every
`ktlintFormat` run (every `make build`/`test`/`check`, per the Makefile)
lowercased the reporter's `RESET`/`GREEN`/`RED`/`CYAN`/`GRAY` constants down
to `reset`/`green`/`red`/`cyan`/`gray` and stripped the fuller rationale
comments. The printed test output itself was never actually wrong -- the
algorithm (`ancestry()`, the shared-prefix dedupe, the blank-line-before-
each-new-suite rule) was already identical to caltrain's -- but the source
no longer matched byte-for-byte. Fixed by adding the disable here (matching
caltrain's own `.editorconfig`) and restoring the SCREAMING_SNAKE_CASE names
+ full comments in the `tasks.withType<Test>` block below. Same fix applied
to huck's copy.

## src/main/kotlin/com/netpress/humane/Humane.kt

### `humanSize`'s parameter type
Takes `Long`, not `Int` -- Kotlin's `Int` is a genuine 32-bit type (unlike
Swift's `Int`, which is 64-bit on real hardware), and the account's own
gigabyte-scale fixture (`5_240_000_000`) already exceeds `Int`'s ~2.1 billion
ceiling. Matches Go's `int64` exactly; Ruby has no fixed-width integer
concern at all.

### Hand-rolled significant-figure math instead of a platform formatter
`humane-swift` delegates straight to `ByteCountFormatter` since macOS/iOS
uniquely ship one that already matches Finder's own wording. The JVM has no
equivalent, so this follows `humane`/`humane-ruby`'s hand-rolled approach
instead: round to 3 significant figures, trim trailing fractional zeros (and
the decimal point itself if nothing survives), unit-label from `sizeUnits`.
See those two repos' own `docs/COMMENTS.md` for the fixture history behind
the 3-significant-figure rule and the zero/byte-scale special cases.

### `formatSignificant` forces `Locale.ROOT`
`String.format("%.2f", value)` without an explicit locale uses the JVM's
default locale, which in several European locales renders a decimal comma
instead of a period ("1,5" instead of "1.5") -- would silently produce wrong
output on a machine with its region set that way. `Locale.ROOT` pins the
formatting to the locale-invariant convention regardless of the runtime
environment.

### `distanceInTime` computes via `Instant.epochSecond`, not `Duration.between(...).seconds`
`Duration`'s internal representation always keeps its nanosecond component
non-negative, so a negative sub-second duration reports a `seconds` value
one lower than the intuitive answer (e.g. -0.5s stores as seconds=-1,
nanos=5e8). Every real fixture here uses whole-second offsets so this edge
case never triggers in practice, but subtracting `epochSecond` directly
sidesteps the ambiguity entirely rather than relying on that coincidence.

### `timeAgo`'s `at` is non-nullable
Matches `humane`'s Go `v0.9.4` change (see that repo's
`docs/releases/v0.9.4.md`), not `humane-ruby`/`humane-swift`'s nullable
`timeAgo`. Every real caller has a concrete timestamp; the nil-handling
belongs to `distanceInTime`'s explicit, fully-tested core, not to the
one-argument convenience wrapping it. `whenNil` stays in `timeAgo`'s
signature only for parity with `distanceInTime`'s option set -- it can never
actually trigger there, same as Go's own vestigial pass-through.

## Test structure

Kotest's `DescribeSpec` gives a real nested `describe`/`context`/`it` tree
natively -- unlike Go's flat `go test -v` output (which needs `gorderly` to
reconstruct the tree from parsed text), Gradle's own `TestDescriptor.parent`
chain already carries the hierarchy, so the custom `TestListener` in
`build.gradle.kts` only has to print it, not rebuild it from scratch.

Each spec follows the same shared-`subject`-plus-`beforeEach` shape as
`humane` (Go)'s `size_test.go`/`time_test.go` and `humane-swift`'s specs: one
`subject` closure declared once per `describe`, each `context` setting the
shared var it reads via `beforeEach` (Kotest's real per-example hook,
mirroring RSpec's `let` laziness) rather than a context-body `val` computed
once at tree-construction time. See `~/workspace/woodie/docs/COWORK.md`'s
"Test structure" section for the account-wide rule this follows.
