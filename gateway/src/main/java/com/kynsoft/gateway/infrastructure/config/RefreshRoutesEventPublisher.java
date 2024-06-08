package com.kynsoft.gateway.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RefreshRoutesEventPublisher {

    private final ApplicationEventPublisher publisher;
    
    private static Boolean enabled;


    @Scheduled(timeUnit=TimeUnit.SECONDS, initialDelayString= "${scheduler.config.initialDelay}", fixedDelayString = "${scheduler.config.refreshRate}")
    public void updateRoutes() {
        if(RefreshRoutesEventPublisher.enabled) {
        	publisher.publishEvent(new RefreshRoutesEvent(this));
        }
    }


    @Value("${scheduler.config.enabled}")
    public void setIsEnableSync(Boolean enabled) {
    	RefreshRoutesEventPublisher.enabled = enabled;
    }
}
