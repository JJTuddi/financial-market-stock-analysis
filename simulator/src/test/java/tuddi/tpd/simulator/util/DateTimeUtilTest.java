package tuddi.tpd.simulator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilTest {

    static final List<Arguments> goodFormatDates = List.of(
            Arguments.of("01-01-1999", LocalDate.of(1999, 1, 1)),
            Arguments.of("10-12-2025", LocalDate.of(2025, 12, 10)),
            Arguments.of("31-12-2023", LocalDate.of(2023, 12, 31)),
            Arguments.of("28-02-2000", LocalDate.of(2000, 2, 28)),
            Arguments.of("29-02-2000", LocalDate.of(2000, 2, 29))
    );

    static final List<Arguments> wrongFormatDates = List.of(
            Arguments.of("30-30-1999", "Couldn't parse the date [30-30-1999]"),
            Arguments.of("29-13-1999", "Couldn't parse the date [29-13-1999]"),
            Arguments.of("1-1-1970", "Couldn't parse the date [1-1-1970]"),
            Arguments.of("12-30-1970", "Couldn't parse the date [12-30-1970]")
    );

    @ParameterizedTest
    @FieldSource("goodFormatDates")
    void givenAnEligibleDates_shouldParseIt(String date, LocalDate expected) {
        LocalDate actual = DateTimeUtil.fromString(date);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @FieldSource("wrongFormatDates")
    void givenAnWrongFormatDates_shouldThrowAnException(String date, String expectedExceptionMessage) {
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> DateTimeUtil.fromString(date));
        assertThat(thrown)
                .hasMessage(expectedExceptionMessage)
                .hasRootCauseInstanceOf(DateTimeException.class);
    }

}