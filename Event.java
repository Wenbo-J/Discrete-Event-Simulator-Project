abstract class Event {
    protected final Customer customer;
    protected final double occurTime;
    protected final Shop shop;

    Event(Customer customer, double occurTime, Shop shop) {
        this.customer = customer;
        this.occurTime = occurTime;
        this.shop = shop;
    }

    double getTime() {
        return occurTime;
    }

    Customer getCustomer() {
        return customer;
    }

    abstract Pair<Event, Shop> nextEvent(Shop shop);

    abstract int priority();

    public String toString() {
        return String.format("%.3f ", this.getTime());
    }
}
