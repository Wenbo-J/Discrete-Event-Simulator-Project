class SelfCheckServer extends Server {
    private final int numOfSelfChecks;
    private final ImList<Counter> counters;
    private static final double LARGE_NUM = 1000000.0;
    private static final int ERROR = -100;

    private SelfCheckServer(double occupiedUntil, 
            int name, 
            ServerQueue queue, 
            int numOfSelfChecks, 
            ImList<Counter> counters) {

        super(occupiedUntil, name, queue);

        this.numOfSelfChecks = numOfSelfChecks;
        this.counters = counters;
    }

    static SelfCheckServer initiate(int name, int numOfSelfChecks, int qmax) {

        ImList<Counter> counters = new ImList<>();

        for (int i = name; i < name + numOfSelfChecks; i++) {
            counters = counters.add(new Counter(0.0, i));
        }

        return new SelfCheckServer(0.0, name, new ServerQueue(qmax), numOfSelfChecks, counters);
    }


    boolean canServeNewCustomer(double customerArrivalTime) {
        for (int i = 0; i < counters.size(); i++) {
            Counter currCounter = counters.get(i);
            if (currCounter.canServeNewCustomer(customerArrivalTime)) {
                return true;
            }
        }
        return false;
    }


    SelfCheckServer update(double customerArrivalTime, double leavingTime) {
        for (int i = 0; i < counters.size(); i++) {
            Counter currCounter = counters.get(i);
            if (currCounter.canServeNewCustomer(customerArrivalTime)) {
                Counter updatedCounter = currCounter.update(leavingTime);
                ImList<Counter> updatedCounters = counters.set(i, updatedCounter);

                return new SelfCheckServer(leavingTime, 
                        super.name, 
                        super.queue, 
                        this.numOfSelfChecks, 
                        updatedCounters);
            }
        }
        return this; // not supposed to happen
    }

    SelfCheckServer update(ServerQueue newQueue) {
        return new SelfCheckServer(super.occupiedUntil, 
                super.name, newQueue, 
                this.numOfSelfChecks, 
                this.counters);
    }


    SelfCheckServer updateRest(double restTime) {
        return this;
    }

    double restTime() {
        return 0.0;
    }

    ServerQueue getQueue() {
        return super.queue;
    }

    double getTime() {
        // method 1
        // return -200.0;
        // not supposed to happen too
        // the only place this is used is in BackFromRestEvent (which selfcheck shouldn't do)
        // and it's also quite difficult to implement this here

        double minSoFar = LARGE_NUM;
        for (int i = 0; i < counters.size(); i++) {
            Counter currCounter = counters.get(i);
            if (currCounter.getTime() < minSoFar) {
                minSoFar = currCounter.getTime();
            }
        }
        return minSoFar;

        // method 3
        // return super.occupiedUntil;
    }

    boolean inRest() {
        return false;
    }

    int actualServeID(double customerArrivalTime) {
        for (int i = 0; i < counters.size(); i++) {
            Counter currCounter = counters.get(i);
            if (currCounter.canServeNewCustomer(customerArrivalTime)) {
                return currCounter.getID();
            }
        }
        return ERROR; // not supposed to occur
    }

    boolean isHuman() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("self-check %d", name);
        // HERE, this needs to be changed potentially since the name here 
        // is not the individual counters' IDs
    }
}
