package bloop.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests an event's inclusive date range. */
public class EventTest {
    private final Event multiDayEvent = new Event("orientation", LocalDateTime.of(2026, 1, 16, 9, 0),
            LocalDateTime.of(2026, 1, 18, 17, 0));

    @Test
    public void occursOn_datesAtAndWithinRange_returnsTrue() {
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 1, 16)));
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 1, 17)));
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2026, 1, 18)));
    }

    @Test
    public void occursOn_datesOutsideRange_returnsFalse() {
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2026, 1, 15)));
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2026, 1, 19)));
    }
}
