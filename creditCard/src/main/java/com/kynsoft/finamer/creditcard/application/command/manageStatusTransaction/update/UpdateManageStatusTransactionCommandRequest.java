package com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateManageStatusTransactionCommandRequest {

    private String session;
    private String employee;
}
