import java.util.function.Supplier;

class Server {
    protected final double occupiedUntil;
    protected final int name;
    private static final double CLOSE_TO_ZERO_CONSTANT = 1E-15;
    protected final ServerQueue queue;
    protected final Supplier<Double> restTimes;

    Server(int name) {
        this(0.0, name, new ServerQueue());
    }

    Server(double occupiedUntil, int name) {
        this(occupiedUntil, name, new ServerQueue());
    }

    Server(double occupiedUntil, int name, ServerQueue queue) {
        this(occupiedUntil, name, queue, () -> -1.0);
    }

    Server(double occupiedUntil, 
            int name, 
            ServerQueue queue,
            Supplier<Double> restTimes) {
        this.occupiedUntil = occupiedUntil;    
        this.name = name;
        this.queue = queue;
        this.restTimes = restTimes;
    }

    ServerQueue getQueue() {
        return this.queue;
    }
    
    double getTime() {
        return this.occupiedUntil;
    }

    boolean canServeNewCustomer(double customerArrivalTime) {
        return this.occupiedUntil - customerArrivalTime <= CLOSE_TO_ZERO_CONSTANT; 
    }

    //Event nextServeEvent(Customer customer, double time, int serverID) {
    //    return new ServeEvent(customer, time, serverID);
    //}

    int getID() {
        return this.name;
    }

    Server update(double customerArrivalTime, double leavingTime) {
        // update time
        return new Server(leavingTime, this.name, this.queue, this.restTimes);
    }

    Server update(ServerQueue newQueue) {
        // update queue
        return new Server(this.occupiedUntil, this.name, newQueue, this.restTimes);
    }

    Server updateRest(double restTime) {
        return new Server(this.occupiedUntil + restTime, 
                this.name, 
                this.queue, 
                this.restTimes);
    }

    double restTime() {
        return this.restTimes.get();
    }

    boolean inRest() {
        if (restTime() > 0.0) {
            return true;
        }
        return false;
    }

    int actualServeID(double customerArrivalTime) {
        return this.getID();
    }

    boolean isHuman() {
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
