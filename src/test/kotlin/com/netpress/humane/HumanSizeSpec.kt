package com.netpress.humane

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HumanSizeSpec :
    DescribeSpec({
        describe("Humane.humanSize") {
            var bytes = 0L
            val subject = { Humane.humanSize(bytes) }

            context("with 0 bytes") {
                beforeEach { bytes = 0 }

                it("formats as Zero KB, matching ByteCountFormatter's own wording") {
                    subject() shouldBe "Zero KB"
                }
            }

            context("with 1 byte") {
                beforeEach { bytes = 1 }

                it("spells out the singular unit") {
                    subject() shouldBe "1 byte"
                }
            }

            context("with a small byte count") {
                beforeEach { bytes = 7 }

                it("spells out bytes rather than using a B label") {
                    subject() shouldBe "7 bytes"
                }
            }

            context("with 999 bytes") {
                beforeEach { bytes = 999 }

                it("stays in bytes, just under the 1 KB threshold") {
                    subject() shouldBe "999 bytes"
                }
            }

            context("with the shared 79992-byte fixture used by lambada scandalous") {
                beforeEach { bytes = 79_992 }

                it("formats as 80 KB") {
                    subject() shouldBe "80 KB"
                }
            }

            context("with a real file's byte count") {
                beforeEach { bytes = 225_935 }

                it("matches Finder's reported size") {
                    subject() shouldBe "226 KB"
                }
            }

            context("with zouk's ByteCountFormatter(.file) fixture") {
                beforeEach { bytes = 500_000 }

                it("matches its output") {
                    subject() shouldBe "500 KB"
                }
            }

            context("with a single-digit megabyte value") {
                beforeEach { bytes = 1_500_000 }

                it("shows one decimal place, trailing zero trimmed") {
                    subject() shouldBe "1.5 MB"
                }
            }

            context("with a gigabyte-scale value") {
                beforeEach { bytes = 5_240_000_000 }

                it("keeps 2 decimal places at 3 significant figures (not truncated to 1)") {
                    subject() shouldBe "5.24 GB"
                }
            }

            context("with a value that lands on an exact unit") {
                beforeEach { bytes = 2_000_000 }

                it("trims both trailing decimal digits") {
                    subject() shouldBe "2 MB"
                }
            }
        }
    })
