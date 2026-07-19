.PHONY: build test lint format check

build:
	./gradlew build -x test

# clean, not just test -- Gradle otherwise marks the test task UP-TO-DATE on
# an unchanged run and skips re-executing it, which also skips the custom
# TestListener reporter's output entirely (it only prints on real
# execution). Matches next-caltrain-kotlin's test.sh (./gradlew clean test).
test:
	./gradlew clean test

lint:
	./gradlew ktlintCheck

# Auto-fixes the mechanical stuff ktlintCheck flags (parameter-list wrapping,
# supertype-on-newline, multiline-expression placement, etc.) -- this repo's
# build.gradle.kts was copied into huck verbatim and huck's own `make build`
# turned up several such violations there that this repo's `make lint` was
# never actually run against; same fix applies here if it turns up the same.
format:
	./gradlew ktlintFormat

check:
	./gradlew clean check
