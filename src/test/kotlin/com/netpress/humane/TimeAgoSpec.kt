package com.netpress.humane

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

// Humane.timeAgo is a thin one-argument convenience over distanceInTime,
// supplying Instant.now() as relativeTo -- see DistanceInTimeSpec for the
// exhaustive wording/bucket coverage this doesn't need to repeat. at is
// non-nullable (see Humane.kt), so there's no nil-handling case here, unlike
// humane-ruby/humane-swift's timeAgo.
class TimeAgoSpec :
    DescribeSpec({
        describe("Humane.timeAgo") {
            context("just now") {
                it("displays less than a minute ago") {
                    Humane.timeAgo(Instant.now()) shouldBe "less than a minute ago"
                }
            }

            context("3 minutes ago") {
                it("forwards to distanceInTime with Instant.now() as relativeTo") {
                    Humane.timeAgo(Instant.now().minusSeconds(180)) shouldBe "3 minutes ago"
                }
            }
        }
    })
