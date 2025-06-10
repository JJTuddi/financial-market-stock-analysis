package tuddi.tpd.simulator.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeUtil {

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static LocalDate fromString(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException parseException) {
            throw new RuntimeException("Couldn't parse the date [" + date + "]", parseException);
        }
    }

    private DateTimeUtil() {

    }
}
