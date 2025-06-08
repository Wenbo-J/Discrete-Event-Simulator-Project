package simulator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.function.Supplier;

public class SimulatorTest {
    @Test
    void testBasicSimulationNoRest() {
        ImList<Pair<Double, Supplier<Double>>> inputTimes = new ImList<>();
        for (int i = 0; i < 3; i++) {
            inputTimes = inputTimes.add(new Pair<>((double) i, () -> 1.0));
        }
        Simulator sim = new Simulator(1, 0, 1, inputTimes, () -> 0.0);
        String result = sim.simulate();
        assertTrue(result.contains("["));
    }

    @Test
    void testSimulationWithRest() {
        ImList<Pair<Double, Supplier<Double>>> inputTimes = new ImList<>();
        for (int i = 0; i < 3; i++) {
            inputTimes = inputTimes.add(new Pair<>((double) i, () -> 1.0));
        }
        Simulator sim = new Simulator(1, 0, 1, inputTimes, () -> 2.0);
        String result = sim.simulate();
        assertTrue(result.contains("["));
    }

    @Test
    void testSimulationEdgeCaseZeroCustomers() {
        ImList<Pair<Double, Supplier<Double>>> inputTimes = new ImList<>();
        Simulator sim = new Simulator(1, 0, 1, inputTimes, () -> 0.0);
        String result = sim.simulate();
        assertTrue(result.contains("["));
    }
} 