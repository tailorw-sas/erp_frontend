package com.kynsoft.finamer.settings.application.command.manageLanguage.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManagerLanguageRequest {

    private String description;
    private Status status;
    private String name;
    private Boolean isEnabled;
    private Boolean defaults;
}
