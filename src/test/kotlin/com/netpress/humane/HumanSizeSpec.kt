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

                it("shows two decimal places") {
                    result shouldBe "5.24 GB"
                }
            }

            context("with a value that lands on an exact unit") {
                beforeEach { bytes = 2_000_000 }

                it("trims both trailing decimal digits") {
                    result shouldBe "2 MB"
                }
            }

            context("with a kilobyte value under 10") {
                beforeEach { bytes = 1_234 }

                it("rounds to whole kilobytes") {
                    result shouldBe "1 KB"
                }
            }

            context("with a kilobyte value that has a fraction") {
                beforeEach { bytes = 2_579 }

                it("rounds to the nearest kilobyte") {
                    result shouldBe "3 KB"
                }
            }

            context("with a megabyte value under 10") {
                beforeEach { bytes = 4_132_383 }

                it("shows one decimal place, not two") {
                    result shouldBe "4.1 MB"
                }
            }

            context("with a megabyte value whose second decimal would round") {
                beforeEach { bytes = 5_243_887 }

                it("drops the second decimal place") {
                    result shouldBe "5.2 MB"
                }
            }

            context("with a megabyte value at 100 or more") {
                beforeEach { bytes = 123_456_789 }

                it("still shows one decimal place") {
                    result shouldBe "123.5 MB"
                }
            }

            context("with a gigabyte value at 10 or more") {
                beforeEach { bytes = 12_345_678_901 }

                it("still shows two decimal places") {
                    result shouldBe "12.35 GB"
                }
            }

            context("with a terabyte value") {
                beforeEach { bytes = 1_234_567_890_123 }

                it("shows two decimal places") {
                    result shouldBe "1.23 TB"
                }
            }

            context("when rounding reaches 1000 of a unit") {
                context("just below the kilobyte limit") {
                    beforeEach { bytes = 999_499 }

                    it("stays in KB") {
                        result shouldBe "999 KB"
                    }
                }

                context("with 999999 bytes") {
                    beforeEach { bytes = 999_999 }

                    it("moves up to MB") {
                        result shouldBe "1 MB"
                    }
                }

                context("just below the megabyte limit") {
                    beforeEach { bytes = 999_949_999 }

                    it("stays in MB") {
                        result shouldBe "999.9 MB"
                    }
                }

                context("with 999960000 bytes") {
                    beforeEach { bytes = 999_960_000 }

                    it("moves up to GB") {
                        result shouldBe "1 GB"
                    }
                }

                context("with 999995000000 bytes") {
                    beforeEach { bytes = 999_995_000_000 }

                    it("moves up to TB") {
                        result shouldBe "1 TB"
                    }
                }
            }
        }
    })
