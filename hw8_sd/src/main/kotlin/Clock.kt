import java.time.Instant
import java.time.temporal.ChronoUnit

interface IClock {
    val now: Instant

    companion object {
        fun minutesFromNow(now: Instant, other: Instant): Long {
            return ChronoUnit.MINUTES.between(other, now)
        }
    }
}

class Clock : IClock {
    override val now: Instant get() = Instant.now()
}

