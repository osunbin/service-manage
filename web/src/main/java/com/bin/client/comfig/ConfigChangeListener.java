package com.bin.client.comfig;

import com.bin.webmonitor.model.ServerConfig;

public interface ConfigChangeListener {

    void onConfigChange(String serviceName, ServerConfig serverConfig);

}
