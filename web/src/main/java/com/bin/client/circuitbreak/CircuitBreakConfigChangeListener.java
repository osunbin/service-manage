package com.bin.client.circuitbreak;

import com.bin.webmonitor.model.CallerCircuitBreakConfigMeta.CircuitBreakConfigMeta;

public interface CircuitBreakConfigChangeListener {

    void onConfigChange(String service, String method, CircuitBreakConfigMeta circuitBreakConfigMeta);

}
