package com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateManageStatusTransactionCommandRequest {

    private String session;
    private String employee;
    private UUID employeeId;
}
