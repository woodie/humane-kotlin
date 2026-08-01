package com.netpress.humane

import com.netpress.kwick.justBeforeEach
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class HumanSizeSpec :
    DescribeSpec({
        describe("Humane.humanSize") {
            var bytes = 0L
            lateinit var result: String
            justBeforeEach { result = Humane.humanSize(bytes) }

            context("with 0 bytes") {
                beforeEach { bytes = 0 }

                it("formats as Zero KB") {
                    result shouldBe "Zero KB"
                }
            }

            context("with 1 byte") {
                beforeEach { bytes = 1 }

                it("spells out the singular unit") {
                    result shouldBe "1 byte"
                }
            }

            context("with a small byte count") {
                beforeEach { bytes = 7 }

                it("spells out bytes") {
                    result shouldBe "7 bytes"
                }
            }

            context("with 999 bytes") {
                beforeEach { bytes = 999 }

                it("stays in bytes") {
                    result shouldBe "999 bytes"
                }
            }

            context("with 79992 bytes") {
                beforeEach { bytes = 79_992 }

                it("formats as 80 KB") {
                    result shouldBe "80 KB"
                }
            }

            context("with a real file's byte count") {
                beforeEach { bytes = 225_935 }

                it("formats as 226 KB") {
                    result shouldBe "226 KB"
                }
            }

            context("with 500000 bytes") {
                beforeEach { bytes = 500_000 }

                it("formats as 500 KB") {
                    result shouldBe "500 KB"
                }
            }

            context("with a single-digit megabyte value") {
                beforeEach { bytes = 1_500_000 }

                it("shows one decimal place, trailing zero trimmed") {
                    result shouldBe "1.5 MB"
                }
            }

            context("with a gigabyte-scale value") {
                beforeEach { bytes = 5_240_000_000 }

                it("keeps 2 decimal places at 3 significant figures (not truncated to 1)") {
                    result shouldBe "5.24 GB"
                }
            }

            context("with a value that lands on an exact unit") {
                beforeEach { bytes = 2_000_000 }

                it("trims both trailing decimal digits") {
                    result shouldBe "2 MB"
                }
            }
        }
    })
