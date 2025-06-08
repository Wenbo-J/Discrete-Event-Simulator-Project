package simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ImListTest {
    @Test
    void testAddAndGet() {
        ImList<Integer> list = new ImList<>();
        list = list.add(1);
        list = list.add(2);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    void testSet() {
        ImList<Integer> list = new ImList<>();
        list = list.add(1);
        list = list.add(2);
        ImList<Integer> updated = list.set(1, 5);
        assertEquals(5, updated.get(1));
        assertEquals(2, list.get(1)); // Immutability
    }

    @Test
    void testRemove() {
        ImList<Integer> list = new ImList<>();
        list = list.add(1);
        list = list.add(2);
        ImList<Integer> updated = list.remove(0);
        assertEquals(2, updated.get(0));
    }

    @Test
    void testSize() {
        ImList<Integer> list = new ImList<>();
        assertEquals(0, list.size());
        list = list.add(1);
        assertEquals(1, list.size());
    }
} 