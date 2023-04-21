import java.util.Scanner;
import java.util.function.Supplier;
import java.util.Random;
import java.util.stream.Stream;

class Main {
    private static final Random RNG_REST = new Random(3L);
    private static final Random RNG_REST_PERIOD = new Random(4L);
    private static final double SERVER_REST_RATE = 0.1;

    static double genRestPeriod() {
        return -Math.log(RNG_REST_PERIOD.nextDouble()) / SERVER_REST_RATE;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ImList<Pair<Double,Supplier<Double>>> inputTimes = 
            new ImList<Pair<Double,Supplier<Double>>>();

        int numOfServers = sc.nextInt();
        int numOfSelfChecks = sc.nextInt();
        int qmax = sc.nextInt();
        int numOfCustomers = sc.nextInt();
        double probRest = sc.nextDouble();

        inputTimes = new ImList<Pair<Double,Supplier<Double>>>(
                Stream.<Pair<Double, Supplier<Double>>>generate(() -> 
                    new Pair<Double, Supplier<Double>>(
                        sc.nextDouble(), () -> sc.nextDouble()))
                .limit(numOfCustomers)
                .toList());

        Supplier<Double> restTimes = () ->
            RNG_REST.nextDouble() < probRest ? genRestPeriod() : 0.0;

        Simulator sim = new Simulator(numOfServers, numOfSelfChecks, qmax, inputTimes, restTimes);
        System.out.println(sim.simulate());
        sc.close();
    }
}
