.PHONY: build test lint format check

# ktlintFormat runs first in its own Gradle invocation, so it's fully done before
# build/check runs ktlintCheck against the result -- huck hit this exact problem
# (build kept failing until `make format` was run manually first) because listing
# both tasks in one gradlew call doesn't guarantee that ordering.
build:
	./gradlew ktlintFormat
	./gradlew build -x test

# clean, not just test -- Gradle otherwise marks the test task UP-TO-DATE on
# an unchanged run and skips re-executing it, which also skips the custom
# TestListener reporter's output entirely (it only prints on real
# execution). Matches next-caltrain-kotlin's test.sh (./gradlew clean test).
test:
	./gradlew ktlintFormat
	./gradlew clean test

# Check-only, no formatting -- fails loudly on style violations instead of
# silently fixing them.
lint:
	./gradlew ktlintCheck

# Auto-fixes the mechanical stuff ktlintCheck flags (parameter-list wrapping,
# supertype-on-newline, multiline-expression placement, etc.). build/test/check
# already run this first; call it directly only if you want formatting alone.
format:
	./gradlew ktlintFormat

check:
	./gradlew ktlintFormat
	./gradlew clean check
