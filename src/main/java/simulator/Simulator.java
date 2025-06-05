package simulator;
import java.util.function.Supplier;

public class Simulator {
    private final int numOfServers;
    private final int numOfSelfChecks;
    private final int qmax;
    private final ImList<Pair<Double,Supplier<Double>>> inputTimes;
    private final Supplier<Double> restTimes;
    private static final int SERVER_NEED_REST = -2;
    private static final double SMALL_NUMBER = 0.000001;

    public Simulator(int numOfServers, int qmax, ImList<Pair<Double,Supplier<Double>>> inputTimes) {
        this(numOfServers, qmax, inputTimes, () -> 0.0);
    }

    public Simulator(int numOfServers,
            int qmax,
            ImList<Pair<Double,Supplier<Double>>> inputTimes,
            Supplier<Double> restTimes) {
        this(numOfServers, 0, qmax, inputTimes, restTimes);
    }

    public Simulator(int numOfServers,
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

    public String simulate() {

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

            Shop shopBeforeEventSpecificHandling = shop; // Initial shop state for this event iteration

            // Allow the event its own chance to update shop and suggest a next event
            // (though for DoneEvent, nextEvent() currently just returns itself and original shop)
            Pair<Event, Shop> pairES = event.nextEvent(shopBeforeEventSpecificHandling);
            Event theNextEventFromEventItself = pairES.first();
            shop = pairES.second(); // Apply shop changes from event.nextEvent()

            if (event instanceof DoneEvent) {
                // A service has just completed. Update statistics for the served customer.
                shop = shop.withNewStats(shop.getStats().incrementCustServed());

                int serverID = ((DoneEvent) event).getServerID();
                Server currentServerState = shop.accessParticularServer(serverID);
                
                // This self-check logic might need review if self-checks are to rest individually.
                // For now, it ensures serverID points to the main SelfCheckServer if applicable.
                if (!currentServerState.isHuman()) {
                    serverID = currentServerState.getID(); 
                }

                // Determine server's next action (rest, serve from queue, or idle)
                Pair<Customer,Shop> pairCS = shop.findNextForDoneEvent(serverID); 
                Customer nextCustomerSignal = pairCS.first();
                shop = pairCS.second(); // Shop is further updated by findNextForDoneEvent

                if (nextCustomerSignal.getID() > -1) { // Actual next customer from queue
                    Server serverForNextServe = shop.accessParticularServer(serverID);
                    double serviceStartTime = Math.max(event.getTime(), serverForNextServe.getTime()); // Serve when server is free and customer done
                    
                    Event newServeEvent = new ServeEvent(nextCustomerSignal,
                            serviceStartTime, // Serve when server actually becomes free
                            serverForNextServe.actualServeID(serviceStartTime), true, shop);
                    eventsPQ = eventsPQ.add(newServeEvent);
                } else if (nextCustomerSignal.getID() == SERVER_NEED_REST) { // Server went resting
                    Server restingServer = shop.accessParticularServer(serverID);
                    Event newBackFromRestEvent = new BackFromRestEvent(new Customer(SERVER_NEED_REST), // Dummy customer for BackFromRestEvent
                            restingServer.getTime(), // This is the future time when rest ends
                            restingServer.getID(), shop);
                    eventsPQ = eventsPQ.add(newBackFromRestEvent);
                }
                // If nextCustomerSignal.getID() == -1, server becomes idle, no new event scheduled from here.
            }

            // shop = updatedShop; // This was the old way, shop is updated incrementally now

            if (!event.toString().equals("")) {
                System.out.println(event);
            }

            if (theNextEventFromEventItself != event) { // If event.nextEvent() scheduled a new, different event
                eventsPQ = eventsPQ.add(theNextEventFromEventItself);
            }
        }

        return shop.statsToString();
    }

}
