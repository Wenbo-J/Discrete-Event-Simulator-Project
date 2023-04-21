class Customer {
    private final int id;
    private final double arrivalTime;
    private final Lazy<Double> serviceTime;

    Customer(int id) {
        this(id, 0.0, Lazy.<Double>of(0.0));
    }

    Customer(int id, double arrivalTime, Lazy<Double> serviceTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    int getID() {
        return id;
    }

    double getArrivalTime() {
        return arrivalTime;
    }

    Lazy<Double> getServiceTime() {
        return serviceTime;
    }

    double doneTime() {
        return arrivalTime + serviceTime.get();
        // not sure if the above would work - does it fit the inputs' order
    }

    public String toString() {
        return String.format("%d", id);
    }
}
