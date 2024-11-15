package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTransactionStatusDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private List<ManageTransactionStatusDto> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;

    private boolean sentStatus;
    private boolean refundStatus;
    private boolean receivedStatus;
    private boolean cancelledStatus;
    private boolean declinedStatus;
    private boolean reconciledStatus;
}