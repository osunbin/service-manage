package com.bin.client.degrade;

@FunctionalInterface
public interface CircuitBreakerListener {

    void listen(MetricTimeWindow.CircuitBreakerEvent event);
}
