import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

class SimulatorTest {
    @Test
    void testMultiDigitServerId() {
        ImList<Pair<Double,Supplier<Double>>> input = new ImList<>();
        for (int i = 0; i < 10; i++) {
            input = input.add(new Pair<>(0.0, () -> 1.0));
        }
        Simulator sim = new Simulator(10, 0, 1, input, () -> 0.0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        try {
            sim.simulate();
        } finally {
            System.setOut(old);
        }
        String output = baos.toString();
        assertTrue(output.contains("10 done serving"));
    }
}
