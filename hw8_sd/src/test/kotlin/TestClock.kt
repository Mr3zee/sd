import java.time.Instant

class TestClock(private var _now: Instant) : IClock {
    override val now: Instant get() = _now

    fun setNow(newNow: Instant) {
        _now = newNow
    }
}
