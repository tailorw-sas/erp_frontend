package com.tailorw.tcaInnsist.application.command.tcaCatalog.validateCatalog;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ValidateCatalogMessage implements ICommandMessage {
    private final UUID id;

    private final String command = "CREATE_ATTACHMENT";

    public ValidateCatalogMessage(UUID id){
        this.id = id;
    }
}
