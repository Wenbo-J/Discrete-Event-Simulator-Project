package com.example.simserver;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

    public SimulationResult() {
    }

    public SimulationResult(int servers, int selfChecks, int qmax, int customers, String result) {
        this.servers = servers;
        this.selfChecks = selfChecks;
        this.qmax = qmax;
        this.customers = customers;
        this.result = result;
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
}
