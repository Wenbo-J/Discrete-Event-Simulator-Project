package com.example.simserver;

public class SystemMetrics {
    private double averageWait;
    private long totalRuns;

    public SystemMetrics(double averageWait, long totalRuns) {
        this.averageWait = averageWait;
        this.totalRuns = totalRuns;
    }

    public double getAverageWait() {
        return averageWait;
    }

    public long getTotalRuns() {
        return totalRuns;
    }
}
