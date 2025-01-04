package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateManageStatusTransactionBlueCommandRequest {
    private Long orderNumber;
    private String cardNumber;
    private String merchantResponse;
    private String isoCode;
    private ETransactionResultStatus status;
    private LocalDateTime paymentDate;
    private String employee;
    private String responseCodeMessage;
    private UUID employeeId;
}
