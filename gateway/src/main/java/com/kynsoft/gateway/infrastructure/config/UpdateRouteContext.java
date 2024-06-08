package com.kynsoft.gateway.infrastructure.config;

import com.kynsoft.gateway.domain.dto.RouteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class UpdateRouteContext implements ApplicationListener<RefreshRoutesEvent>, Ordered {

    private final DiscoveryClient discoveryClient;

    private final RouteDefinitionsContext definitionsContext;


    public RouteDefinitionsContext updateDefinitions() {

        List<String> services = discoveryClient.getServices();

        definitionsContext.clear();

        for (String serviceId : services) {
            if (!serviceId.toUpperCase().equals("UNKNOWN")) {
                List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
                for (ServiceInstance instance : instances) {
//                    String path = instance.getUri().toString();
//                    URI uri;
//                    try {
//                        path = path.replace("http", "https");
//                        uri = new URI(path);
//                    } catch (URISyntaxException e) {
//                        throw new RuntimeException(e);
//                    }
//                    definitionsContext.add(serviceId.toLowerCase(), new RouteDTO(serviceId.toLowerCase(), "/" + serviceId.toLowerCase() + "/**", uri));

                    definitionsContext.add(serviceId.toLowerCase(), new RouteDTO(serviceId.toLowerCase(), "/" + serviceId.toLowerCase() + "/**", instance.getUri()));
                }
            }
        }

        return definitionsContext;
    }

    @Override
    public void onApplicationEvent(RefreshRoutesEvent event) {
        this.updateDefinitions();
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public RouteDefinitionsContext getDefinitionsContext() {
        return definitionsContext;
    }

}
