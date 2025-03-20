package com.kynsoft.finamer.payment.application.command.manageCountry.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageCountryCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Boolean isDefault;
    private String status;
    private UUID language;
    private String iso3;

    public CreateManageCountryCommand(UUID id,
                                      String code,
                                      String name,
                                      String description,
                                      Boolean isDefault,
                                      String status,
                                      UUID language,
                                      String iso3
                                      ){
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.isDefault = isDefault;
        this.status = status;
        this.language = language;
        this.iso3 = iso3;

    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageCountryMessage(id);
    }
}
