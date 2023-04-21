class ServeEvent extends Event {
    private final int serverID;
    private final boolean prevWasDone;

    private static final int SERVE_PRIO = 1;

    ServeEvent(Customer customer, 
            double serviceTime, 
            int serverID, 
            boolean prevWasDone, 
            Shop shop) {
        super(customer, serviceTime, shop);
        this.serverID = serverID;
        this.prevWasDone = prevWasDone;
    }

    @Override
    Pair<Event, Shop> nextEvent(Shop shop) {
        Event newEvent = new DoneEvent(customer,
                this.smartServeUntil(),
                serverID, shop);
        // the moment of dequeueing + how long customer takes

        Shop updatedShop = shop.nextForServeEvent(serverID,
                this.waitTime(),
                this.smartServeUntil(), 
                super.getTime(), 
                this.prevWasDone);
        return new Pair<Event, Shop>(newEvent, updatedShop);
    }

    double smartServeUntil() {
        // smart function that returns when the server is
        // occupied until. it takes care of waiting time.

        Lazy<Double> cachedSupp = customer.getServiceTime();
        return super.getTime() + cachedSupp.get();
    }

    double waitTime() {
        return super.getTime() - customer.getArrivalTime();
    }

    int priority() {
        return SERVE_PRIO;
    }

    public String toString() {
        String serverStr = shop.accessParticularServer(serverID).isHuman() 
            ? String.valueOf(serverID) : String.format("self-check %d", serverID);
        return super.toString() 
            + customer 
            + " serves by " 
            + serverStr;
    }
}
