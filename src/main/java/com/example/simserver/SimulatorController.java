package com.example.simserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import simulator.Simulator;
import simulator.ImList;
import simulator.Pair;

import java.util.function.Supplier;

@RestController
public class SimulatorController {

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
        return sim.simulate();
    }
}
