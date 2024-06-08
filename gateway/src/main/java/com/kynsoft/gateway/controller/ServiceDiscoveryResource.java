//package com.kynsoft.gateway.controller;
//
//import com.kynsoft.gateway.domain.dto.ServiceInstanceDTO;
//import com.kynsoft.gateway.infrastructure.config.RefreshRoutesEventPublisher;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cloud.client.DefaultServiceInstance;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class ServiceDiscoveryResource {
//
//    private final DiscoveryClient discoveryClient;
//
//    private final SimpleDiscoveryProperties simpleDiscoveryProperties;
//
//    private final Environment env;
//
//    private final RefreshRoutesEventPublisher publisher;
//
//    @GetMapping("/services/instances")
//    public ResponseEntity<List<ServiceInstance>> getAllServiceInstances() {
//        Map<String, List<ServiceInstance>> instances = discoveryClient
//            .getServices()
//            .stream()
//            .collect(Collectors.toMap(Function.identity(), discoveryClient::getInstances));
//        List<ServiceInstance> res = instances.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
//
//
//
//    @PostMapping("/services/instances")
//    public ResponseEntity<ServiceInstance> addStaticServiceInstance(@Valid @RequestBody ServiceInstanceDTO serviceInstanceDTO)
//        throws URISyntaxException, IOException {
//       // if (Arrays.stream(this.env.getActiveProfiles()).anyMatch("static"::equals)) {
//            // create static instance
//            URI uri = new URI(serviceInstanceDTO.getUrl());
//            DefaultServiceInstance staticInstance = new DefaultServiceInstance();
//            staticInstance.setUri(uri);
//            staticInstance.setServiceId(serviceInstanceDTO.getServiceId());
//            staticInstance.setInstanceId(serviceInstanceDTO.getServiceId());
//
//            // add static instance in our discovery client
//            Map<String, List<DefaultServiceInstance>> instances = new HashMap<>();
//            instances.putAll(simpleDiscoveryProperties.getInstances());
//            instances.put(staticInstance.getServiceId(), List.of(staticInstance));
//            simpleDiscoveryProperties.setInstances(instances);
//
//            // send a spring application event to refresh beans
//            publisher.updateRoutes();
//
//            return new ResponseEntity<>(staticInstance, HttpStatus.CREATED);
//       /* } else {
//            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
//        }*/
//    }
//
//
//    @GetMapping("/services/{serviceId}")
//    public ResponseEntity<List<ServiceInstance>> getAllServiceInstancesForServiceId(@PathVariable String serviceId) {
//        return new ResponseEntity<>(discoveryClient.getInstances(serviceId), HttpStatus.OK);
//    }
//
//
//    @DeleteMapping("/services/{serviceId}")
//    public ResponseEntity<Void> removeStaticServiceInstance(@PathVariable String serviceId) {
//        if (Arrays.stream(this.env.getActiveProfiles()).anyMatch("static"::equals)) {
//            // remove static instance in our discovery client
//            simpleDiscoveryProperties.getInstances().remove(serviceId);
//
//            // send a spring application event to refresh beans
//            publisher.updateRoutes();
//
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
//        }
//    }
//}