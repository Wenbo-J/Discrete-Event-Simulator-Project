
Overview

The project is a Java-based discrete‑event simulator. The entry point is Main.java, which reads simulation parameters from standard input and constructs a Simulator instance. The simulator sets up servers, self‑check counters, and the initial event queue. Then it repeatedly processes events until the queue is empty.

class Main {
    …
    Simulator sim = new Simulator(numOfServers, numOfSelfChecks, qmax, inputTimes, restTimes);
    System.out.println(sim.simulate());
    …
}

Core components

    Simulator

        Maintains the list of servers, initializes events, and runs the main loop that polls events from a priority queue and processes them.

        During simulation it updates the Shop (the system state) and appends new events when necessary.

ImList<Server> servers = …
PQ<Event> eventsPQ = new PQ<Event>(new EventComparator());
Statistics stats = new Statistics(inputTimes.size());
Shop shop = new Shop(servers, stats);
…
while (!eventsPQ.isEmpty()) {
    Pair<Event, PQ<Event>> pair = eventsPQ.poll();
    Event event = pair.first();
    …
    Pair<Event, Shop> pairES = event.nextEvent(shop);
    …
    if (theNextEvent != event) {
        eventsPQ = eventsPQ.add(theNextEvent);
    }
}
return shop.statsToString();

    Events

        All events extend the abstract Event class, which stores the customer, event time, and a reference to the Shop.

abstract class Event {
    protected final Customer customer;
    protected final double occurTime;
    protected final Shop shop;
    …
    abstract Pair<Event, Shop> nextEvent(Shop shop);
    abstract int priority();
}

    Key event types include ArriveEvent, ServeEvent, WaitEvent, DoneEvent, LeaveEvent, and BackFromRestEvent. Each implements nextEvent and priority. For instance, ServeEvent schedules a DoneEvent and updates the Shop with waiting time information:

Pair<Event, Shop> nextEvent(Shop shop) {
    Event newEvent = new DoneEvent(customer,
            this.smartServeUntil(),
            serverID, shop);
    Shop updatedShop = shop.nextForServeEvent(serverID,
            this.waitTime(),
            this.smartServeUntil(),
            super.getTime(),
            this.prevWasDone);
    return new Pair<Event, Shop>(newEvent, updatedShop);
}

    Shop and Server management

        Shop encapsulates the state of all servers and queue statistics. It determines which event should occur next when a customer arrives or when a service completes.

Pair<String, Shop> findNextForArriveEvent(Customer customer) {
    int idleServerID = idleServer(customer);
    if (idleServerID == -1) {
        int notFullQueueID = notFullQueue();
        if (notFullQueueID == -1) {
            return new Pair<>("LEAVE", this);
        }
        …
        return new Pair<>("WAIT", new Shop(updatedServers, stats));
    }
    …
    return new Pair<>("SERVE", new Shop(updatedServers, updatedStats));
}

    Servers can be human servers (Server) or self‑checkout counters (SelfCheckServer). Self‑checkout counters manage a list of Counter objects:

static SelfCheckServer initiate(int name, int numOfSelfChecks, int qmax) {
    ImList<Counter> counters = new ImList<>();
    for (int i = name; i < name + numOfSelfChecks; i++) {
        counters = counters.add(new Counter(0.0, i));
    }
    return new SelfCheckServer(0.0, name, new ServerQueue(qmax), numOfSelfChecks, counters);
}

    Immutable Data Structures

        ImList and PQ are immutable wrappers around ArrayList and PriorityQueue. They return new instances when elements are added or removed, supporting functional-style updates.

public ImList<E> add(E elem) {
    ImList<E> newList = new ImList<E>(this.elems);
    newList.elems.add(elem);
    return newList;
}

public PQ<E> add(E element) {
    PQ<E> copy = new PQ<E>(this);
    copy.pq.add(element);
    return copy;
}

    Statistics

        Captures total waiting time, number of customers served, and customers who left. At the end of the simulation, the result is formatted by Statistics.toString().

return String.format("[%.3f %d %d]",
        averageWaitTime(),
        numCustServed,
        numCustLeft);

Running the simulation

Main expects input parameters describing server counts, queue capacity, number of customers, and per‑customer arrival/service times. It also uses random numbers to decide if a server rests between customers. After building the list of events, it prints the formatted statistics at the end.

Next steps / things to explore

    Extend event types: Add new types of events (e.g., customer reneging) by creating subclasses of Event and adjusting the event loop.

    Optimize or refactor: Examine Simulator.simulate and Shop methods to streamline state updates or remove redundant steps.

    Test and validate: Implement unit tests (e.g., using JUnit) to ensure each component behaves correctly, especially the immutable data structures.

    Scalability: Explore more sophisticated scheduling algorithms or multi-threaded versions if you want to simulate larger systems.

This overview should help you navigate the repository, understand how the event-driven simulation is structured, and offer ideas for future learning and development.



Spring Boot REST API
--------------------
A simple Spring Boot server is included under `src/main/java/com/example/simserver`.
It exposes a `/simulate` endpoint that runs the simulator with default
parameters and returns the formatted statistics string.

Build the project with Gradle and run the application:

```
./gradlew bootRun
```

The endpoint can be tested locally using `curl`:

```
curl 'http://localhost:8080/simulate'
```
