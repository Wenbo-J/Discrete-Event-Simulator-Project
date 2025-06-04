package simulator;
class ArriveEvent extends Event {
    private static final int ARRIVE_PRIO = 4;

    ArriveEvent(Customer customer, double arrivalTime, Shop shop) {
        super(customer, arrivalTime, shop);
    }

    Pair<Event, Shop> nextEvent(Shop shop) {
        Pair<String, Shop> pair = shop.findNextForArriveEvent(customer);
        String typeOfEvent = pair.first();
        Shop updatedShop = pair.second();
        Event newEvent;
        if (typeOfEvent.equals("LEAVE")) {
            newEvent = new LeaveEvent(customer, super.getTime(), shop);
        } else if (typeOfEvent.equals("WAIT")) {
            
            int queueID = shop.notFullQueue();
            // above must get from original "shop" otherwise stackoverflow
            Server currServer = updatedShop.accessParticularServer(queueID);
            newEvent = new WaitEvent(customer, super.getTime(), currServer, updatedShop);
        } else {
            // (typeOfEvent.equals("SERVE")) 
            int idleServerID = shop.idleServer(customer);
            // above must get from original "shop" otherwise stackoverflow
            
            Server currServer = shop.accessParticularServer(idleServerID).isHuman() ? 
                updatedShop.accessParticularServer(idleServerID) :
                shop.accessParticularServer(idleServerID);
            
            newEvent = new ServeEvent(customer, 
                super.getTime(), 
                currServer.actualServeID(customer.getArrivalTime()), false, shop); 
        }
        return new Pair<Event, Shop>(newEvent, updatedShop);
    }

    int priority() {
        return ARRIVE_PRIO;
    }

    public String toString() {
        return super.toString() + customer + " arrives";
    }
}
