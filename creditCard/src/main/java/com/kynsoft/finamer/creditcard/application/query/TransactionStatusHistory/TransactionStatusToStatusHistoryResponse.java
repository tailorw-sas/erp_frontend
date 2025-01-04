package com.kynsoft.finamer.creditcard.application.query.TransactionStatusHistory;


import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TransactionStatusToStatusHistoryResponse {

    private UUID id;
    private String code;
    private String name;

    public TransactionStatusToStatusHistoryResponse(ManageTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
