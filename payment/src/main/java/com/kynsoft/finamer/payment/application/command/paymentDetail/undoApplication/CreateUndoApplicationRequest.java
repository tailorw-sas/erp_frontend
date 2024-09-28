package com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplication;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUndoApplicationRequest {

    private UUID paymentDetail;
}
