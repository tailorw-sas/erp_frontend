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
public class ManageReconcileTransactionStatusDto {
    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean requireValidation;
    private List<ManageReconcileTransactionStatusDto> relatedStatuses;

}
