package com.kynsoft.finamer.settings.application.command.managerCountry.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerCountryCommand implements ICommand {

    private UUID id;
    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;
    private UUID managerLanguage;
    private Status status;

    public UpdateManagerCountryCommand(UUID id, String name, String description, String dialCode, 
                                       String iso3, Boolean isDefault, UUID managerLanguage, Status status) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.dialCode = dialCode;
        this.iso3 = iso3;
        this.isDefault = isDefault;
        this.managerLanguage = managerLanguage;
        this.status = status;
    }

    public static UpdateManagerCountryCommand fromRequest(UpdateManagerCountryRequest request, UUID id) {
        return new UpdateManagerCountryCommand(
                id,
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
        return new UpdateManagerCountryMessage(id);
    }
}
