package com.example.simserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping; // Not used yet
import java.util.List;
import java.util.Random;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simulator.Simulator;
import simulator.ImList;
import simulator.Pair;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class SimulatorController {

    private final SimulationResultRepository repository;
    private final Random random = new Random();

    private static final Pattern RESULT_PATTERN = Pattern.compile("\\[(?<avg>[\\d.]+)\\s+(?<served>\\d+)\\s+(?<left>\\d+)\\]");

    @Autowired
    public SimulatorController(SimulationResultRepository repository) {
        this.repository = repository;
    }

    private double generateExponentialVariate(double mean) {
        if (mean <= 0) return 1.0; // Avoid issues with log(0) or negative means, default to 1
        return -mean * Math.log(1.0 - random.nextDouble());
    }

    private SimulationResult parseAndCreateResult(int servers, int selfChecks, int qmax, int customers, String resultString) {
        Matcher m = RESULT_PATTERN.matcher(resultString);
        if (m.find()) {
            double avg = Double.parseDouble(m.group("avg"));
            int served = Integer.parseInt(m.group("served"));
            int left = Integer.parseInt(m.group("left"));
            return new SimulationResult(servers, selfChecks, qmax, customers, resultString, avg, served, left);
        } else {
            // Fallback or throw exception if pattern does not match
            return new SimulationResult(servers, selfChecks, qmax, customers, resultString, null, null, null);
        }
    }

    @GetMapping("/simulate")
    public SimulationResult simulate(@RequestParam(defaultValue = "1") int servers,
                                 @RequestParam(defaultValue = "0") int selfChecks,
                                 @RequestParam(defaultValue = "1") int qmax,
                                 @RequestParam(defaultValue = "10") int customers,
                                 @RequestParam(defaultValue = "0.5") double meanArrivalInterval,
                                 @RequestParam(defaultValue = "1.0") double meanServiceTime,
                                 @RequestParam(defaultValue = "0.0") double meanRestTime
                                ) {

        ImList<Pair<Double, Supplier<Double>>> inputTimes = new ImList<>();
        double currentArrivalTime = 0.0;

        for (int i = 0; i < customers; i++) {
            if (i > 0) { // First customer arrives at t=0 or a small random offset if desired
                currentArrivalTime += generateExponentialVariate(meanArrivalInterval);
            }
            // Service time supplier for this customer
            Supplier<Double> serviceTimeSupplier = () -> generateExponentialVariate(meanServiceTime);
            inputTimes = inputTimes.add(new Pair<>(currentArrivalTime, serviceTimeSupplier));
        }
        
        Supplier<Double> actualRestTimesSupplier;
        if (meanRestTime <= 0) {
            actualRestTimesSupplier = () -> 0.0;
        } else {
            actualRestTimesSupplier = () -> generateExponentialVariate(meanRestTime);
        }

        Simulator sim = new Simulator(servers, selfChecks, qmax, inputTimes, actualRestTimesSupplier);
        String resultString = sim.simulate();
        
        SimulationResult simResult = parseAndCreateResult(servers, selfChecks, qmax, customers, resultString);
        return repository.save(simResult);
    }

    @GetMapping("/simulations")
    public java.util.List<SimulationResult> allResults() {
        return repository.findAll();
    }

    @GetMapping("/simulations/{id}")
    public SimulationResult getResult(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @GetMapping("/metrics")
    public SystemMetrics metrics() {
        List<SimulationResult> all = repository.findAll();
        if (all.isEmpty()) {
            return new SystemMetrics(0.0, 0);
        }
        double sumOfAverages = 0.0;
        int validResultsCount = 0;

        for (SimulationResult r : all) {
            if (r.getAverageWaitTime() != null) {
                sumOfAverages += r.getAverageWaitTime();
                validResultsCount++;
            }
        }
        if (validResultsCount == 0) {
            return new SystemMetrics(0.0, all.size());
        }
        return new SystemMetrics(sumOfAverages / validResultsCount, all.size());
    }
}

