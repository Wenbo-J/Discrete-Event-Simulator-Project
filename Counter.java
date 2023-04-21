class Counter {
    private final int name;
    private final double occupiedUntil;
    private static final double CLOSE_TO_ZERO_CONSTANT = 1E-15;


    Counter(double occupiedUntil, int name) {
        this.occupiedUntil = occupiedUntil;
        this.name = name;
    }

    boolean canServeNewCustomer(double customerArrivalTime) {
        return this.occupiedUntil - customerArrivalTime <= CLOSE_TO_ZERO_CONSTANT; 
    }

    double getTime() {
        return this.occupiedUntil;
    }

    int getID() {
        return this.name;
    }

    Counter update(double leavingTime) {
        // update time
        return new Counter(leavingTime, this.name);
    }
}
