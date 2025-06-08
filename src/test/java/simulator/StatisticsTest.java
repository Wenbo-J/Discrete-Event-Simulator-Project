package simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatisticsTest {
    @Test
    void testIncrementTotalWaitTime() {
        Statistics stats = new Statistics(5);
        Statistics updated = stats.incrementTotalWaitTime(2.5);
        assertTrue(updated.hasNonZeroWaitTime());
        assertFalse(updated.noCustomersWaited());
    }

    @Test
    void testIncrementCustServed() {
        Statistics stats = new Statistics(2);
        stats = stats.incrementCustServed();
        stats = stats.incrementCustServed();
        assertTrue(stats.allCustomersWereServed());
    }

    @Test
    void testIncrementCustLeft() {
        Statistics stats = new Statistics(5);
        Statistics updated = stats.incrementCustLeft();
        assertTrue(updated.anyCustomersLeft());
    }

    @Test
    void testStatsToString() {
        Statistics stats = new Statistics(2);
        stats = stats.incrementTotalWaitTime(2.0);
        stats = stats.incrementCustServed();
        String result = stats.toString();
        assertTrue(result.startsWith("[1.000"));
    }
} 