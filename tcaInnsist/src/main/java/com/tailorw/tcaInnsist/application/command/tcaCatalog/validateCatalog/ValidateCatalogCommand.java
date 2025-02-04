package com.tailorw.tcaInnsist.application.command.tcaCatalog.validateCatalog;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import java.util.UUID;

public class ValidateCatalogCommand implements ICommand {

    private UUID id;

    public ValidateCatalogCommand(){
        this.id = UUID.randomUUID();
    }

    @Override
    public ICommandMessage getMessage() {
        return new ValidateCatalogMessage(id);
    }
}
