package com.kynsof.audit.infrastructure.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class SecurityContextThreadLocalFilter implements WebFilter {
    private Logger logger = LoggerFactory.getLogger(SecurityContextThreadLocalFilter.class);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
       return ReactiveSecurityContextHolder.getContext().flatMap(securityContext ->{
           if(Objects.nonNull(securityContext.getAuthentication())) {
               logger.info(securityContext.getAuthentication().getName());
               UserContext.setCurrentUser(securityContext.getAuthentication().getName());
           }
           return chain.filter(exchange);
        }).switchIfEmpty(Mono.defer(() -> {
            // Cuando el usuario NO está autenticado
            logger.info("No authenticated user found");
           // UserContext.clear(); // o cualquier otra lógica para usuarios no autenticados
            return chain.filter(exchange);
        }));

    }
}
