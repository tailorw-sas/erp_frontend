package com.kynsoft.finamer.settings.application.command.manageLanguage.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManagerLanguageRequest {

    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean isEnabled;
    private Boolean defaults;
}
