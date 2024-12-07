package com.kynsoft.finamer.creditcard.application.query.bankReconciliationStatusHistory;


import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReconcileStatusToStatusHistoryResponse {

    private UUID id;
    private String code;
    private String name;

    public ReconcileStatusToStatusHistoryResponse(ManageReconcileTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
