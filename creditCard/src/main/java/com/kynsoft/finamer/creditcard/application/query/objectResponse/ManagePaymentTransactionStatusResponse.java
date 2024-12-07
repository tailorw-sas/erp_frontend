package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePaymentTransactionStatusResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private Status status;
    private String description;
    private boolean requireValidation;
    private List<ManagePaymentTransactionStatusSimpleResponse> navigate;

    private boolean inProgress;
    private boolean completed;
    private boolean cancelled;
    private boolean applied;

    public ManagePaymentTransactionStatusResponse(ManagePaymentTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.inProgress = dto.isInProgress();
        this.completed = dto.isCompleted();
        this.cancelled = dto.isCancelled();
        this.applied = dto.isApplied();
        this.requireValidation = dto.isRequireValidation();
        this.navigate = dto.getNavigate() != null ? dto.getNavigate().stream().map(ManagePaymentTransactionStatusSimpleResponse::new).collect(Collectors.toList()) : null;
    }
}
