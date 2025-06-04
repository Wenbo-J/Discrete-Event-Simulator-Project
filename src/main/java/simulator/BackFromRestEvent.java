package simulator;
class BackFromRestEvent extends Event {
    private final int serverID;

    BackFromRestEvent(Customer customer, double time, int serverID, Shop shop) {
        super(customer, time, shop);
        this.serverID = serverID;
    }

    @Override
    Pair<Event,Shop> nextEvent(Shop shop) {
        Server currServer = shop.accessParticularServer(serverID); 
        ServerQueue currQueue = currServer.getQueue();
        
        if (currQueue.isEmpty()) {
            return new Pair<Event,Shop>(this, shop);
        }

        Pair<Customer,ServerQueue> pair = currQueue.dequeue();
        Customer nextCustomer = pair.first();
        ServerQueue updatedQueue = pair.second();

        // here, the server is gonna rest, and serve the next inqueue cust
        // after server finishes resting

        Server updatedServer = currServer.update(updatedQueue);
        ImList<Server> updatedServers = shop.updateServers(serverID, updatedServer);
        Event newEvent = new ServeEvent(nextCustomer,
                updatedServer.getTime(),
                updatedServer.getID(), false, shop);
        Statistics updatedStatistics = shop.getStats().incrementCustServed();
        Shop updatedShop = new Shop(updatedServers, updatedStatistics);
        return new Pair<Event,Shop>(newEvent, updatedShop);
    }

    @Override
    int priority() {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }
}
