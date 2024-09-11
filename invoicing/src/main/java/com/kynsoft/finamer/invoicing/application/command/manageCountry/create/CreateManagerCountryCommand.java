package com.kynsoft.finamer.invoicing.application.command.manageCountry.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class CreateManagerCountryCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;
    private UUID managerLanguage;
    private Status status;

    public CreateManagerCountryCommand(UUID id,String code, String name, String description, String dialCode,
                                       String iso3, Boolean isDefault, UUID managerLanguage, Status status) {
        this.id = Objects.nonNull(id)?id:UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.name = name;
        this.dialCode = dialCode;
        this.iso3 = iso3;
        this.isDefault = isDefault;
        this.managerLanguage = managerLanguage;
        this.status = status;
    }

    public static CreateManagerCountryCommand fromRequest(CreateManagerCountryRequest request) {
        return new CreateManagerCountryCommand(
                null,
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getDialCode(),
                request.getIso3(),
                request.getIsDefault(),
                request.getManagerLanguage(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerCountryMessage(id);
    }
}
