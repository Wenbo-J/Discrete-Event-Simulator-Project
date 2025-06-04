import java.util.function.Supplier;

class Simulator {
    private final int numOfServers;
    private final int numOfSelfChecks;
    private final int qmax;
    private final ImList<Pair<Double,Supplier<Double>>> inputTimes;
    private final Supplier<Double> restTimes;
    private static final int SERVER_NEED_REST = -2;
    private static final double SMALL_NUMBER = 0.000001;

    Simulator(int numOfServers, int qmax, ImList<Pair<Double,Supplier<Double>>> inputTimes) {
        this(numOfServers, qmax, inputTimes, () -> 0.0);
    }

    Simulator(int numOfServers,
            int qmax,
            ImList<Pair<Double,Supplier<Double>>> inputTimes,
            Supplier<Double> restTimes) {
        this(numOfServers, 0, qmax, inputTimes, restTimes);
    }

    Simulator(int numOfServers,
            int numOfSelfChecks,
            int qmax,
            ImList<Pair<Double,Supplier<Double>>> inputTimes,
            Supplier<Double> restTimes) {
        this.numOfServers = numOfServers;
        this.numOfSelfChecks = numOfSelfChecks;
        this.qmax = qmax;
        this.inputTimes = inputTimes;
        this.restTimes = restTimes;
    }

    String simulate() {

        ImList<Server> servers = new ImList<Server>();
        for (int i = 1; i < numOfServers + 1; i++) {
            servers = servers.add(new Server(0.0, i, 
                        new ServerQueue(qmax), restTimes));
        }

        if (numOfSelfChecks > 0) {
            int selfCheckName = numOfServers + 1;
            servers = servers.add(SelfCheckServer.initiate(selfCheckName, numOfSelfChecks, qmax));
        }

        PQ<Event> eventsPQ = new PQ<Event>(new EventComparator()); 

        Statistics stats = new Statistics(inputTimes.size());
        Shop shop = new Shop(servers, stats);

        for (int i = 0; i < inputTimes.size(); i++) {
            Pair<Double,Supplier<Double>> pair = inputTimes.get(i);
            double arrivalTime = pair.first();
            Supplier<Double> serviceTime = pair.second();
            Customer customer = new Customer(i + 1,
                    arrivalTime,
                    Lazy.<Double>of(serviceTime));
            Event event = new ArriveEvent(customer, arrivalTime, shop);
            eventsPQ = eventsPQ.add(event);
        }
        while (!eventsPQ.isEmpty()) {  
            Pair<Event, PQ<Event>> pair = eventsPQ.poll();
            Event event = pair.first();
            eventsPQ = pair.second();
            // update the server with the second() of pair returned from
            // nextEvent() by set() the ImList<Server> and 
            // reassign the new ImList<Server> to the current one
            // figure out which server is free here?

            Pair<Event, Shop> pairES = event.nextEvent(shop);
            Event theNextEvent = pairES.first();
            Shop updatedShop = pairES.second();

            if (event instanceof DoneEvent) {
                int serverID = ((DoneEvent) event).getServerID();
                Server eventServer = shop.accessParticularServer(serverID);
                if (!eventServer.isHuman()) {
                    serverID = eventServer.getID();
                }


                Pair<Customer,Shop> pairCS = shop.findNextForDoneEvent(serverID);
                Customer nextCustomer = pairCS.first();
                updatedShop = pairCS.second();
                if (nextCustomer.getID() > -1) {
                    // create new ServeEvent IF AND ONLY IF the Server's queue has any customer left
                    Server updatedServer = updatedShop.accessParticularServer(serverID);

                    double timeNow = updatedServer.getTime() + SMALL_NUMBER;

                    Event newEvent = new ServeEvent(nextCustomer,
                            updatedServer.getTime(),
                            updatedServer.actualServeID(timeNow), true, shop);

                    // updatedShop = updatedShop.updateServers(serverID, updatedServer, 
                    // nextCustomer, timeNow);

                    eventsPQ = eventsPQ.add(newEvent);
                } else if (nextCustomer.getID() == SERVER_NEED_REST) {
                    // server went resting
                    Server updatedServer = updatedShop.accessParticularServer(serverID);
                    Event newEvent = new BackFromRestEvent(nextCustomer,
                            updatedServer.getTime(),
                            updatedServer.getID(), updatedShop);
                    eventsPQ = eventsPQ.add(newEvent);
                }
            }

            shop = updatedShop;

            if (!event.toString().equals("")) {
                // this check above is to ensure no empty line is printed
                // if BackFromRestEvent is the nextEvent
                // Don't know how to have a better way for now...

                System.out.println(event);
            }
            // see if the below needs update
            if (theNextEvent != event) {
                eventsPQ = eventsPQ.add(theNextEvent);
            }
        }

        return shop.statsToString();
    }

}
