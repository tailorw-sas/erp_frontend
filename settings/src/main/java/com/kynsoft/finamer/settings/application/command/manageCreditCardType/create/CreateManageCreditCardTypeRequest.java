package com.kynsoft.finamer.settings.application.command.manageCreditCardType.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageCreditCardTypeRequest {
    private String code;
    private String name;
    private String description;
    private Integer firstDigit;
    private Status status;
}
