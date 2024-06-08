package com.kynsoft.finamer.settings.application.command.managerCountry.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerCountryRequest {
    private String code;
    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;
    private UUID managerLanguage;
    private Status status;
}
