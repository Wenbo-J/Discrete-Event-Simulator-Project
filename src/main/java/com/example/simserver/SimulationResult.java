package com.example.simserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SimulationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int servers;
    private int selfChecks;
    private int qmax;
    private int customers;
    private String result;

    // New fields for parsed metrics
    private Double averageWaitTime;
    private Integer customersServed;
    private Integer customersLeft;
    private LocalDateTime simulationTimestamp;

    public SimulationResult() {
    }

    public SimulationResult(int servers, int selfChecks, int qmax, int customers, String result,
                            Double averageWaitTime, Integer customersServed, Integer customersLeft) {
        this.servers = servers;
        this.selfChecks = selfChecks;
        this.qmax = qmax;
        this.customers = customers;
        this.result = result;
        this.averageWaitTime = averageWaitTime;
        this.customersServed = customersServed;
        this.customersLeft = customersLeft;
        this.simulationTimestamp = LocalDateTime.now(); // Set timestamp on creation
    }

    public Long getId() {
        return id;
    }

    public int getServers() {
        return servers;
    }

    public int getSelfChecks() {
        return selfChecks;
    }

    public int getQmax() {
        return qmax;
    }

    public int getCustomers() {
        return customers;
    }

    public String getResult() {
        return result;
    }

    public Double getAverageWaitTime() {
        return averageWaitTime;
    }

    public Integer getCustomersServed() {
        return customersServed;
    }

    public Integer getCustomersLeft() {
        return customersLeft;
    }

    public LocalDateTime getSimulationTimestamp() {
        return simulationTimestamp;
    }
}
