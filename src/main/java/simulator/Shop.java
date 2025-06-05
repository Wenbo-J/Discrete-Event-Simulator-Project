package simulator;
class Shop {
    private final ImList<Server> servers;
    private final Statistics stats;
    private static final int SERVER_NEED_REST = -2;

    Shop(ImList<Server> servers, Statistics stats) {
        this.servers = servers;
        this.stats = stats;
    }

    // Method to create a new Shop instance with updated statistics
    public Shop withNewStats(Statistics newStats) {
        return new Shop(this.servers, newStats);
    }

    Pair<String, Shop> findNextForArriveEvent(Customer customer) {
        String newEvent;
        ImList<Server> updatedServers;
        Statistics updatedStats = this.stats;
        int idleServerID = idleServer(customer);
        if (idleServerID == -1) {
            int notFullQueueID = notFullQueue();
            if (notFullQueueID == -1) {
                newEvent = "LEAVE";
                return new Pair<String, Shop>(newEvent, this);
            }
   
            newEvent = "WAIT";
            Pair<Server, ImList<Server>> pair;
            pair = enqueueCustomer(notFullQueueID, customer);
            updatedServers = pair.second();
            return new Pair<String, Shop>(newEvent, new Shop(updatedServers, stats));
        } 

        Server currServer = servers.get(idleServerID - 1);
        newEvent = "SERVE";
        Server updatedServer = currServer.update(customer.getArrivalTime(), customer.doneTime());
        updatedServers = servers.set(idleServerID - 1, updatedServer);
        updatedStats = stats.incrementCustServed();
        return new Pair<String, Shop>(newEvent, new Shop(updatedServers, updatedStats));
    }

    Server accessParticularServer(int id) {
        if (id > servers.size()) {
            id = servers.size();
        }
        return servers.get(id - 1);
    }

    ImList<Server> updateServers(int id, Server serverToBeUpdated) {
        return this.servers.set(id - 1, serverToBeUpdated);
    }

    // Shop updateServers(int id, Server currServer, Customer customer, double newTime) {
    //     Server updatedServer = currServer.update(newTime, customer.doneTime());
    //     ImList<Server> updatedServers = servers.set(id - 1, updatedServer);
    //     return new Shop(updatedServers, this.stats);
    // }

    Pair<Server, ImList<Server>> enqueueCustomer(int notFullQueueID, Customer c) {
        Server currServer = accessParticularServer(notFullQueueID);
        ServerQueue currQueue = currServer.getQueue();
        ServerQueue updatedQueue = currQueue.enqueue(c);
        Server updatedServer = currServer.update(updatedQueue);
        ImList<Server> updatedServers;
        updatedServers = servers.set(notFullQueueID - 1, updatedServer);
        return new Pair<Server, ImList<Server>>(updatedServer, updatedServers);
    }

    Pair<Customer, Shop> findNextForDoneEvent(int serverID) {
        Server currServer = servers.get(serverID - 1);
        ServerQueue currQueue = currServer.getQueue();
        double actualRestDuration = currServer.restTime(); // This calls our supplier

        if (actualRestDuration > 0.0) {
            // Server is scheduled to rest
            Server restingServer = currServer.updateRest(actualRestDuration);
            ImList<Server> updatedServersList = servers.set(serverID - 1, restingServer);
            Shop shopAfterRestSignal = new Shop(updatedServersList, this.stats);
            return new Pair<>(new Customer(SERVER_NEED_REST), shopAfterRestSignal);
        } else {
            // Server is NOT scheduled to rest (actualRestDuration is 0.0 or less)
            if (currQueue.isEmpty()) {
                // No rest, and queue is empty. Server becomes idle.
                // Shop state (servers, stats) doesn't change for this specific decision step.
                return new Pair<>(new Customer(-1), this);
            } else {
                // No rest, and queue is NOT empty. Server will serve the next customer from its queue.
                Pair<Customer, ServerQueue> dequeuedPair = currQueue.dequeue();
                Customer nextCustomerToServe = dequeuedPair.first();
                ServerQueue newQueueForServer = dequeuedPair.second();

                Server serverWithNewQueue = currServer.update(newQueueForServer);
                ImList<Server> updatedServersList = servers.set(serverID - 1, serverWithNewQueue);

                // Stats are not updated here; assumed to be handled by the event processing loop
                // for the DoneEvent that just occurred, or by the new ServeEvent.
                Shop shopForNextServe = new Shop(updatedServersList, this.stats);
                return new Pair<>(nextCustomerToServe, shopForNextServe);
            }
        }
    }

    Shop nextForServeEvent(int serverID, 
        double waitTime, 
        double sSUntil, 
        double customerArrivalTime, 
        boolean prevWasDone) {

        if (serverID > servers.size()) {
            serverID = servers.size();
        }
        Server updatedServer = accessParticularServer(serverID);
        if (updatedServer.isHuman() || prevWasDone) {
            updatedServer = updatedServer.update(customerArrivalTime, sSUntil);
            // only update "twice" if it's a human
            // because updating more than once messes up with non-human servers

            // OR, if previous event is DoneEvent
        }
        ImList<Server> updatedServers;
        updatedServers = servers.set(updatedServer.getID() - 1, updatedServer);
        Statistics updatedStats = stats.incrementTotalWaitTime(waitTime);
        return new Shop(updatedServers, updatedStats);
    }

    // Shop updateStatistics() 

    Shop updateStatsForLeave() {
        Statistics updatedStats = stats.incrementCustLeft();
        updatedStats = updatedStats.decrementNumOfCust();
        return new Shop(this.servers, updatedStats);
    }

    // int idleServer(Customer customer) {
    //     return idleServer(customer, false);
    // }

    int idleServer(Customer customer) {
        // returns the id of the first idle server
        // return -1 if all servers are occupied
        for (int i = 0; i < servers.size(); i++) {
            Server currServer = servers.get(i);
            if (currServer.canServeNewCustomer(customer.getArrivalTime())) {
                return i + 1; // id of the first idle server
            }
        }
        return -1; 
    }

    int notFullQueue() {
        // returns the id of the first unfull queue
        // return -1 if all queues are occupied
        for (int i = 0; i < this.servers.size(); i++) {
            if (!this.servers.get(i).getQueue().isFull()) {
                return i + 1; // id of the first not full queue
            }
        }
        return -1;
    }

    Statistics getStats() {
        return this.stats;
    }

    String statsToString() {
        return this.stats.toString();
    }
}
