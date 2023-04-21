import java.util.Comparator;

class EventComparator implements Comparator<Event> {
    private static final double ALMOST_ZERO = 1E-15; 
    
    public int compare(Event e1, Event e2) {
        // have some id like attribute for different event, so instcof can be avoided
        if (Math.abs(e1.getTime() - e2.getTime()) < ALMOST_ZERO) {
            if (e1.priority() < e2.priority()) {
                return -1;
            }
            if (e1.priority() > e2.priority()) {
                return 1;
            }

            if (e1.getCustomer().getID() < e2.getCustomer().getID()) {
                return -1;
            }
            if (e1.getCustomer().getID() > e2.getCustomer().getID()) {
                return 1;
            }

            return 0;
        } 

        if (e1.getTime() - e2.getTime() < 0) {
            return -1;
        } 
        
        return 1;
    }
}
