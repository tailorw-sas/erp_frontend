package com.kynsoft.finamer.settings.application.command.manageCreditCardType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageCreditCardTypeRequest {
    private String name;
    private String description;
    private Integer firstDigit;
    private Status status;
}
