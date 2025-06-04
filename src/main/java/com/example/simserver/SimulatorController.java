package com.example.simserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simulator.Simulator;
import simulator.ImList;
import simulator.Pair;

import java.util.function.Supplier;

@RestController
public class SimulatorController {

    private final SimulationResultRepository repository;

    @Autowired
    public SimulatorController(SimulationResultRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/simulate")
    public String simulate(@RequestParam(defaultValue = "1") int servers,
                           @RequestParam(defaultValue = "0") int selfChecks,
                           @RequestParam(defaultValue = "1") int qmax,
                           @RequestParam(defaultValue = "1") int customers) {

        ImList<Pair<Double, Supplier<Double>>> inputTimes = new ImList<>();
        for (int i = 0; i < customers; i++) {
            inputTimes = inputTimes.add(new Pair<>((double) i, () -> 1.0));
        }
        Simulator sim = new Simulator(servers, selfChecks, qmax, inputTimes, () -> 0.0);
        String result = sim.simulate();
        repository.save(new SimulationResult(servers, selfChecks, qmax, customers, result));
        return result;
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
        double sum = 0.0;
        for (SimulationResult r : all) {
            String result = r.getResult();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\\[(?<avg>[\\d.]+) \\d+ \\d+\\]").matcher(result);
            if (m.find()) {
                sum += Double.parseDouble(m.group("avg"));
            }
        }
        return new SystemMetrics(sum / all.size(), all.size());
    }
}
