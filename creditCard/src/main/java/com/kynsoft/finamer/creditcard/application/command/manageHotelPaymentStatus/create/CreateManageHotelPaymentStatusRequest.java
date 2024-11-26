package com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.create;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageHotelPaymentStatusRequest {

    private String code;
    private String name;
    private Status status;
    private String description;

    private boolean inProgress;
    private boolean completed;
    private boolean cancelled;
    private boolean applied;
}
