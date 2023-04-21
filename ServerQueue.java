class ServerQueue {
    // This ServerQueue is based on Immutable PQ instead of normal Queue
    // it is theoretically achieved by having the customer arrivalTime as the priority metric
    private final PQ<Customer> queue;
    private final int maxCapacity;

    ServerQueue() {
        this(1);
    }

    ServerQueue(int maxCapacity) {
        this(new PQ<Customer>(new CustomerComparator()), maxCapacity);
    }

    
    ServerQueue(PQ<Customer> queue, int maxCapacity) {
        this.queue = queue;
        this.maxCapacity = maxCapacity;
    }

    ServerQueue enqueue(Customer customer) {
        if (this.isFull()) {
            System.out.println("Queue is full, should not add anymore");
        }
        PQ<Customer> updatedQueue = queue.add(customer);
        return new ServerQueue(updatedQueue, maxCapacity);
    }

    Pair<Customer, ServerQueue> dequeue() {
        Pair<Customer, PQ<Customer>> pair = queue.poll();
        Customer finishedCustomer = pair.first();
        PQ<Customer> updatedQueue = pair.second();
        ServerQueue updatedServerQueue = new ServerQueue(updatedQueue, maxCapacity);
        return new Pair<Customer, ServerQueue>(finishedCustomer, updatedServerQueue);
    }

    boolean isFull() {
        int size = 0;
        PQ<Customer> tempQueue = queue;
        while (!tempQueue.isEmpty()) {
            size += 1;
            tempQueue = tempQueue.poll().second();
        }

        return size >= maxCapacity ? true : false;
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }
}
