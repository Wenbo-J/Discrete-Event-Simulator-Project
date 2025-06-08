package simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShopTest {
    @Test
    void testEnqueueCustomer() {
        ImList<Server> servers = new ImList<>();
        servers = servers.add(new Server(0.0, 1, new ServerQueue(2), () -> 0.0));
        Shop shop = new Shop(servers, new Statistics(1));
        Customer c = new Customer(1, 0.0, Lazy.of(() -> 1.0));
        Shop shop2 = shop;
        Pair<Server, ImList<Server>> result = shop2.enqueueCustomer(1, c);
        assertFalse(result.first().getQueue().isEmpty());
    }

    @Test
    void testFindNextForDoneEventRest() {
        ImList<Server> servers = new ImList<>();
        servers = servers.add(new Server(0.0, 1, new ServerQueue(), () -> 2.0));
        Shop shop = new Shop(servers, new Statistics(1));
        Pair<Customer, Shop> result = shop.findNextForDoneEvent(1);
        assertEquals(-2, result.first().getID()); // SERVER_NEED_REST
    }

    @Test
    void testFindNextForDoneEventQueue() {
        ImList<Server> servers = new ImList<>();
        ServerQueue q = new ServerQueue(2);
        q = q.enqueue(new Customer(2, 0.0, Lazy.of(() -> 1.0)));
        servers = servers.add(new Server(0.0, 1, q, () -> 0.0));
        Shop shop = new Shop(servers, new Statistics(1));
        Pair<Customer, Shop> result = shop.findNextForDoneEvent(1);
        assertEquals(2, result.first().getID());
    }

    @Test
    void testUpdateStatsForLeave() {
        ImList<Server> servers = new ImList<>();
        servers = servers.add(new Server(0.0, 1, new ServerQueue(), () -> 0.0));
        Statistics stats = new Statistics(1);
        Shop shop = new Shop(servers, stats);
        Shop updated = shop.updateStatsForLeave();
        assertTrue(updated.getStats().anyCustomersLeft());
    }
} 