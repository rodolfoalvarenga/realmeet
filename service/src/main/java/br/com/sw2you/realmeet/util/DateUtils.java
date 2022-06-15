package br.com.sw2you.realmeet.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static java.time.temporal.ChronoUnit.MILLIS;

public final class DateUtils {

    private static final ZoneOffset DEFAULT_TIMEZONE = ZoneOffset.of("-03:00");

    private DateUtils() {
    }

    // MILLIS ignore nanoseconds that cause error on tests
    public static OffsetDateTime now() {
        return OffsetDateTime.now(DEFAULT_TIMEZONE).truncatedTo(MILLIS);
    }
}
