package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionResultStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
}
