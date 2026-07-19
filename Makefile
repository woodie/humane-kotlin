.PHONY: build test lint check

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

check:
	./gradlew clean check
