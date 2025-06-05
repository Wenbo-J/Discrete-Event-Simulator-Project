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

        Server serverWithNewQueue = currServer.update(updatedQueue);
        ImList<Server> updatedServers = shop.updateServers(serverID, serverWithNewQueue);
        
        Event newServeEvent = new ServeEvent(nextCustomer,
                serverWithNewQueue.getTime(),
                serverWithNewQueue.getID(), false, shop);
        
        Shop finalShop = new Shop(updatedServers, shop.getStats()); 
        return new Pair<Event,Shop>(newServeEvent, finalShop);
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
