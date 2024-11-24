package com.modsen.ratingservice.util;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    private final Map<String, int[]> servicePortsMap;

    public TestServiceInstanceListSupplier(Map<String, int[]> servicePortsMap) {
        this.servicePortsMap = servicePortsMap;
    }

    @Override
    public String getServiceId() {
        return String.join(",", servicePortsMap.keySet());
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        List<ServiceInstance> result = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : servicePortsMap.entrySet()) {
            String serviceId = entry.getKey();
            int[] ports = entry.getValue();
            for (int i = 0; i < ports.length; i++) {
                result.add(new DefaultServiceInstance(serviceId + i, serviceId, "localhost", ports[i], false));
            }
        }
        return Flux.just(result);
    }
}