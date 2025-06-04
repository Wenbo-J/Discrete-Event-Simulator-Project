package simulator;
class DoneEvent extends Event {
    private final int serverID;

    DoneEvent(Customer customer, double completeTime, int serverID, Shop shop) {
        super(customer, completeTime, shop);
        this.serverID = serverID;
    }

    @Override
    Pair<Event, Shop> nextEvent(Shop shop) {
        return new Pair<Event, Shop>(this, shop);
    }

    int priority() {
        return 1;
    }

    int getServerID() {
        return this.serverID;
    }

    public String toString() {
        String serverStr = shop.accessParticularServer(serverID).isHuman() 
            ? String.valueOf(serverID) : String.format("self-check %d", serverID);
        return String.format("%.3f %s done serving by %s",
                super.getTime(),
                customer,
                serverStr);
    }
}
