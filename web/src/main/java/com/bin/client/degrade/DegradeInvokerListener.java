package com.bin.client.degrade;

import com.bin.client.degrade.MetricTimeWindow.MetricEventType;

public interface DegradeInvokerListener {




   default void onSuccess(String serviceName, String function,long timeCost) {
        ClientDegradeManager.addEvent(serviceName, function, MetricEventType.success);
    }

    default void onTimeout(String serviceName, String function,long timeCost) {
        ClientDegradeManager.addEvent(serviceName, function, MetricEventType.timeout);

    }

    default void onException(String serviceName, String function,long timeCost) {
        ClientDegradeManager.addEvent(serviceName, function, MetricEventType.fail);
    }

}
