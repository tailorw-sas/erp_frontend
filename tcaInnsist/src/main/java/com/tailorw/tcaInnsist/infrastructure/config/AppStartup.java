package com.tailorw.tcaInnsist.infrastructure.config;

import com.tailorw.tcaInnsist.domain.services.ITcaCatalogService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppStartup {

    private final ITcaCatalogService catalogService;

    public AppStartup(ITcaCatalogService catalogService){
        this.catalogService = catalogService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onAppStartup(){
        catalogService.validateCatalog();
    }
}
