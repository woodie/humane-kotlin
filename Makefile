.PHONY: build test lint check

build:
	./gradlew build -x test

test:
	./gradlew test

lint:
	./gradlew ktlintCheck

check:
	./gradlew check
