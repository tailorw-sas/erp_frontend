package com.kynsoft.finamer.invoicing.application.command.manageCountry.update;


import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerCountryRequest {
    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;
    private UUID managerLanguage;
    private Status status;
}
