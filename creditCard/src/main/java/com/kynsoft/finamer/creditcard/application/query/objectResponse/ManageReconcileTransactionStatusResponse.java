package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
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
public class ManageReconcileTransactionStatusResponse implements IResponse {
    private UUID id;
    private String code;
    private Status status;
    private String description;

    private String name;
    private Boolean requireValidation;
    private List<ManageReconcileTransactionStatusResponse> navigate;

    public ManageReconcileTransactionStatusResponse(ManageReconcileTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.requireValidation = dto.getRequireValidation();
        this.navigate = dto.getRelatedStatuses() != null ? dto.getRelatedStatuses().stream().map(ManageReconcileTransactionStatusResponse::new).toList() : null;
    }

}
