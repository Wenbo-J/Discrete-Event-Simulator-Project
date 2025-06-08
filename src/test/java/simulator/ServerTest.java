package simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.function.Supplier;

public class ServerTest {
    @Test
    void testRestTimeSupplier() {
        Server s = new Server(0.0, 1, new ServerQueue(), () -> 2.5);
        assertEquals(2.5, s.restTime(), 1e-9);
    }

    @Test
    void testUpdateRest() {
        Server s = new Server(0.0, 1, new ServerQueue(), () -> 1.0);
        Server rested = s.updateRest(3.0);
        assertEquals(3.0, rested.getTime(), 1e-9);
    }

    @Test
    void testQueueUpdate() {
        ServerQueue q = new ServerQueue();
        Server s = new Server(0.0, 1, q, () -> 0.0);
        ServerQueue q2 = new ServerQueue(5);
        Server updated = s.update(q2);
        assertEquals(q2, updated.getQueue());
    }

    @Test
    void testIsHuman() {
        Server s = new Server(0.0, 1, new ServerQueue(), () -> 0.0);
        assertTrue(s.isHuman());
    }
} 