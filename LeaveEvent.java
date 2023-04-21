class LeaveEvent extends Event {

    LeaveEvent(Customer customer, double leavingTime, Shop shop) {
        super(customer, leavingTime, shop);
    }

    @Override
    Pair<Event, Shop> nextEvent(Shop shop) {
        Shop updatedShop = shop.updateStatsForLeave();
        return new Pair<Event, Shop>(this, updatedShop);
    }

    int priority() {
        return 2;
    }
    
    @Override
    public String toString() {
        return super.toString() + customer + " leaves";
    }
}
