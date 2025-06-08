package simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Comparator;

public class PQTest {
    @Test
    void testAddAndPoll() {
        PQ<Integer> pq = new PQ<>(Comparator.naturalOrder());
        pq = pq.add(3);
        pq = pq.add(1);
        pq = pq.add(2);
        assertFalse(pq.isEmpty());
        Pair<Integer, PQ<Integer>> pair = pq.poll();
        assertEquals(1, pair.first().intValue());
        pq = pair.second();
        pair = pq.poll();
        assertEquals(2, pair.first().intValue());
    }

    @Test
    void testIsEmpty() {
        PQ<Integer> pq = new PQ<>(Comparator.naturalOrder());
        assertTrue(pq.isEmpty());
    }
} 