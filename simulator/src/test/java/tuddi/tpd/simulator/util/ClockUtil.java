package tuddi.tpd.simulator.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public final class ClockUtil {

    public static final Clock FIXED_CLOCK = Clock.fixed(Instant.ofEpochSecond(1749416285L), ZoneId.systemDefault());


    private ClockUtil() {
        throw new RuntimeException("Not meant to be instantiated!");
    }
}
