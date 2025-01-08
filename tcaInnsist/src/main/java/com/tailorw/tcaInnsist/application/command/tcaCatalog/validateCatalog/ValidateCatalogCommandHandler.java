package com.tailorw.tcaInnsist.application.command.tcaCatalog.validateCatalog;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.tcaInnsist.domain.services.ITcaCatalogService;
import org.springframework.stereotype.Component;

@Component
public class ValidateCatalogCommandHandler implements ICommandHandler<ValidateCatalogCommand> {

    private final ITcaCatalogService tcaCatalogService;

    public ValidateCatalogCommandHandler(ITcaCatalogService tcaCatalogService){
        this.tcaCatalogService = tcaCatalogService;
    }

    @Override
    public void handle(ValidateCatalogCommand command) {
        tcaCatalogService.validateCatalog();
    }
}
