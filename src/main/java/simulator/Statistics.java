package simulator;
class Statistics {
    // This class deals with the statistics of the simulation
    private final double totalWaitTime;
    private final int numCustServed;
    private final int numCustLeft;
    private final int numOfCustomers;

    Statistics(int numOfCustomers) {
        this(0.0, 0, 0, numOfCustomers);
    }

    Statistics(double twt, int ncs, int ncl, int numOfCustomers) {
        this.totalWaitTime = twt;
        this.numCustServed = ncs;
        this.numCustLeft = ncl;
        this.numOfCustomers = numOfCustomers;
    }
    
    double averageWaitTime() {
        return totalWaitTime / numOfCustomers;
    }

    Statistics incrementTotalWaitTime(double byHowMuch) {
        return new Statistics(totalWaitTime + byHowMuch,
                this.numCustServed,
                this.numCustLeft,
                this.numOfCustomers);
    }

    Statistics incrementCustServed() {
        return new Statistics(this.totalWaitTime,
                numCustServed + 1,
                this.numCustLeft,
                this.numOfCustomers);
    }

    Statistics incrementCustLeft() {
        return new Statistics(this.totalWaitTime,
                this.numCustServed,
                numCustLeft + 1,
                this.numOfCustomers);
    }

    Statistics decrementNumOfCust() {
        return new Statistics(this.totalWaitTime,
                this.numCustServed,
                this.numCustLeft,
                numOfCustomers - 1);
    }

    public boolean allCustomersWereServed() {
        return numCustServed == numOfCustomers;
    }

    public boolean anyCustomersLeft() {
        return numCustLeft > 0;
    }

    public boolean noCustomersWaited() {
        return totalWaitTime == 0.0;
    }

    public boolean hasNonZeroWaitTime() {
        return totalWaitTime > 0.0;
    }

    @Override
    public String toString() {
        return String.format("[%.3f %d %d]",
                averageWaitTime(),
                numCustServed,
                numCustLeft);
    }
}
