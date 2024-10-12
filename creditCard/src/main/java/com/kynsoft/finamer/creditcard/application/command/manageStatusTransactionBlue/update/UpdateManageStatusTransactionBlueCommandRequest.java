package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateManageStatusTransactionBlueCommandRequest {
    private Long orderNumber;
    private String cardNumber;
    private String merchantResponse;
}
