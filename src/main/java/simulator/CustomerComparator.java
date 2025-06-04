package simulator;
import java.util.Comparator;

class CustomerComparator implements Comparator<Customer> {
    @Override
    public int compare(Customer c1, Customer c2) {
        return Double.compare(c1.getArrivalTime(), c2.getArrivalTime());
    }
}
