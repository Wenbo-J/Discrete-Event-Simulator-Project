class WaitEvent extends Event {
    private final Server server;
    private static final int WAIT_PRIO = 5;

    WaitEvent(Customer customer, double startWaitTime, Server server, Shop shop) {
        super(customer, startWaitTime, shop);
        this.server = server;
    }

    Pair<Event, Shop> nextEvent(Shop shop) { 
        return new Pair<Event, Shop>(this, shop); 
    }

    int priority() {
        return WAIT_PRIO;
    }

    @Override
    public String toString() {
        return super.toString() 
            + customer 
            + " waits at " 
            + server;
    }
}
