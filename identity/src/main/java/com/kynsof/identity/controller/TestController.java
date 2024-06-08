package com.kynsof.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/test")
public class TestController {
	static Logger logger = Logger.getLogger(TestController.class.getName());

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN_CLIENT')")
    public Mono<ResponseEntity<String>> testAdmin(){
    	return Mono.just(ResponseEntity.ok("Keycloak with ADMIN CLIENT ROLE"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER_CLIENT')") 
    public Mono<ResponseEntity<String>> testUser(){
        return Mono.just(ResponseEntity.ok("Keycloak with USER CLIENT ROLE"));
    }
}
