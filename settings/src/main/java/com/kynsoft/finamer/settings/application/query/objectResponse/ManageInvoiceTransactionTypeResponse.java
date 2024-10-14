package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceTransactionTypeResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean isAgencyRateAmount;
    private Boolean isNegative;
    private Boolean isPolicyCredit;
    private Boolean isRemarkRequired;
    private Integer minNumberOfCharacters;
    private String defaultRemark;

    private boolean defaults;

    public ManageInvoiceTransactionTypeResponse(ManageInvoiceTransactionTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.isAgencyRateAmount = dto.getIsAgencyRateAmount();
        this.isNegative = dto.getIsNegative();
        this.isPolicyCredit = dto.getIsPolicyCredit();
        this.isRemarkRequired = dto.getIsRemarkRequired();
        this.minNumberOfCharacters = dto.getMinNumberOfCharacters();
        this.defaultRemark = dto.getDefaultRemark();
        this.defaults = dto.isDefaults();
    }
}
