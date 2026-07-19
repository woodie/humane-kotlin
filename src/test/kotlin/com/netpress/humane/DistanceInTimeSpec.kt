package com.netpress.humane

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class DistanceInTimeSpec :
    DescribeSpec({
        describe("Humane.distanceInTime") {
            val base = Instant.parse("2026-07-08T12:00:00Z")

            describe(
                "with no options (the recommended defaults: approximate true, includeSeconds false -- matching ActionView's own defaults)",
            ) {
                var at = base
                val subject = { Humane.distanceInTime(at, base) }

                context("just now") {
                    beforeEach { at = base }

                    it("displays less than a minute ago") {
                        subject() shouldBe "less than a minute ago"
                    }
                }

                context("45 seconds ago") {
                    beforeEach { at = base.minusSeconds(45) }

                    it("rounds up to 1 minute ago (past the 30-second cutoff)") {
                        subject() shouldBe "1 minute ago"
                    }
                }

                context("1 minute ago") {
                    beforeEach { at = base.minusSeconds(60) }

                    it("displays 1 minute ago, singular") {
                        subject() shouldBe "1 minute ago"
                    }
                }

                context("3 minutes ago") {
                    beforeEach { at = base.minusSeconds(180) }

                    it("displays 3 minutes ago") {
                        subject() shouldBe "3 minutes ago"
                    }
                }

                context("1 hour ago") {
                    beforeEach { at = base.minusSeconds(3600) }

                    it("displays about 1 hour ago") {
                        subject() shouldBe "about 1 hour ago"
                    }
                }

                context("15 hours ago") {
                    beforeEach { at = base.minusSeconds(15 * 3600L) }

                    it("displays about 15 hours ago") {
                        subject() shouldBe "about 15 hours ago"
                    }
                }

                context("30 hours ago") {
                    beforeEach { at = base.minusSeconds(30 * 3600L) }

                    it("rolls up to 1 day ago, with no about (ActionView's table has none on the day bucket)") {
                        subject() shouldBe "1 day ago"
                    }
                }

                context("3 days ago") {
                    beforeEach { at = base.minusSeconds(3 * 86_400L) }

                    it("displays 3 days ago") {
                        subject() shouldBe "3 days ago"
                    }
                }

                context("45 seconds from now") {
                    beforeEach { at = base.plusSeconds(45) }

                    it("rounds up to in 1 minute (past the 30-second cutoff)") {
                        subject() shouldBe "in 1 minute"
                    }
                }

                context("3 minutes from now") {
                    beforeEach { at = base.plusSeconds(180) }

                    it("displays in 3 minutes") {
                        subject() shouldBe "in 3 minutes"
                    }
                }

                context("3 hours from now") {
                    beforeEach { at = base.plusSeconds(3 * 3600L) }

                    it("displays in about 3 hours") {
                        subject() shouldBe "in about 3 hours"
                    }
                }
            }

            describe("with includeSeconds: true") {
                var at = base
                val subject = { Humane.distanceInTime(at, base, includeSeconds = true) }

                context("just now") {
                    beforeEach { at = base }

                    it("displays 0 seconds ago") {
                        subject() shouldBe "0 seconds ago"
                    }
                }

                context("1 second ago") {
                    beforeEach { at = base.minusSeconds(1) }

                    it("displays 1 second ago, singular") {
                        subject() shouldBe "1 second ago"
                    }
                }

                context("45 seconds ago") {
                    beforeEach { at = base.minusSeconds(45) }

                    it("displays 45 seconds ago") {
                        subject() shouldBe "45 seconds ago"
                    }
                }

                context("45 seconds from now") {
                    beforeEach { at = base.plusSeconds(45) }

                    it("displays in 45 seconds") {
                        subject() shouldBe "in 45 seconds"
                    }
                }
            }

            describe("with approximate: false") {
                var at = base
                val subject = { Humane.distanceInTime(at, base, approximate = false) }

                context("1 hour ago") {
                    beforeEach { at = base.minusSeconds(3600) }

                    it("displays the exact count, no about prefix") {
                        subject() shouldBe "1 hour ago"
                    }
                }

                context("15 hours ago") {
                    beforeEach { at = base.minusSeconds(15 * 3600L) }

                    it("displays 15 hours ago") {
                        subject() shouldBe "15 hours ago"
                    }
                }
            }

            describe("nil handling") {
                context("when at is nil and whenNil is set") {
                    it("returns whenNil without formatting") {
                        Humane.distanceInTime(null, base, whenNil = "an unknown time") shouldBe "an unknown time"
                    }
                }

                context("when at is nil and whenNil is left unset") {
                    it("returns an empty string") {
                        Humane.distanceInTime(null, base) shouldBe ""
                    }
                }
            }

            // Boundary regression coverage for ActionView's distance_of_time_in_words bucket table (truncated at the "1 day" row); each context below sits on one cutoff second from that table.
            describe("at the approximate-distance bucket table boundaries") {
                describe("with approximate: false") {
                    var at = base
                    val subject = { Humane.distanceInTime(at, base, approximate = false) }

                    context("29 seconds ago") {
                        beforeEach { at = base.minusSeconds(29) }

                        it("stays less than a minute") {
                            subject() shouldBe "less than a minute ago"
                        }
                    }

                    context("30 seconds ago") {
                        beforeEach { at = base.minusSeconds(30) }

                        it("rounds up to 1 minute") {
                            subject() shouldBe "1 minute ago"
                        }
                    }

                    context("89 seconds ago") {
                        beforeEach { at = base.minusSeconds(89) }

                        it("stays 1 minute") {
                            subject() shouldBe "1 minute ago"
                        }
                    }

                    context("90 seconds ago") {
                        beforeEach { at = base.minusSeconds(90) }

                        it("rounds up to 2 minutes") {
                            subject() shouldBe "2 minutes ago"
                        }
                    }

                    context("44 minutes 29 seconds ago") {
                        beforeEach { at = base.minusSeconds(44 * 60L + 29) }

                        it("stays 44 minutes") {
                            subject() shouldBe "44 minutes ago"
                        }
                    }

                    context("44 minutes 30 seconds ago") {
                        beforeEach { at = base.minusSeconds(44 * 60L + 30) }

                        it("rounds up to 1 hour") {
                            subject() shouldBe "1 hour ago"
                        }
                    }

                    context("89 minutes 29 seconds ago") {
                        beforeEach { at = base.minusSeconds(89 * 60L + 29) }

                        it("stays 1 hour") {
                            subject() shouldBe "1 hour ago"
                        }
                    }

                    context("89 minutes 30 seconds ago") {
                        beforeEach { at = base.minusSeconds(89 * 60L + 30) }

                        it("rounds up to 2 hours") {
                            subject() shouldBe "2 hours ago"
                        }
                    }

                    context("23 hours 59 minutes 29 seconds ago") {
                        beforeEach { at = base.minusSeconds(23 * 3600L + 59 * 60L + 29) }

                        it("stays 24 hours") {
                            subject() shouldBe "24 hours ago"
                        }
                    }

                    context("23 hours 59 minutes 30 seconds ago") {
                        beforeEach { at = base.minusSeconds(23 * 3600L + 59 * 60L + 30) }

                        it("rounds up to 1 day") {
                            subject() shouldBe "1 day ago"
                        }
                    }
                }

                describe("with no options (approximate true by default)") {
                    var at = base
                    val subject = { Humane.distanceInTime(at, base) }

                    context("44 minutes 29 seconds ago") {
                        beforeEach { at = base.minusSeconds(44 * 60L + 29) }

                        it("has no about") {
                            subject() shouldBe "44 minutes ago"
                        }
                    }

                    context("44 minutes 30 seconds ago") {
                        beforeEach { at = base.minusSeconds(44 * 60L + 30) }

                        it("gains about, entering the hour bucket") {
                            subject() shouldBe "about 1 hour ago"
                        }
                    }

                    context("23 hours 59 minutes 29 seconds ago") {
                        beforeEach { at = base.minusSeconds(23 * 3600L + 59 * 60L + 29) }

                        it("keeps about") {
                            subject() shouldBe "about 24 hours ago"
                        }
                    }

                    context("23 hours 59 minutes 30 seconds ago") {
                        beforeEach { at = base.minusSeconds(23 * 3600L + 59 * 60L + 30) }

                        it("drops about, entering the day bucket") {
                            subject() shouldBe "1 day ago"
                        }
                    }
                }
            }
        }
    })
