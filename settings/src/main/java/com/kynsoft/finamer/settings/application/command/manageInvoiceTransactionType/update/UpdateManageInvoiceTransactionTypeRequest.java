package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageInvoiceTransactionTypeRequest {

    private String description;
    private Status status;
    private String name;
    private Boolean isAgencyRateAmount;
    private Boolean isNegative;
    private Boolean isPolicyCredit;
    private Boolean isRemarkRequired;
    private Integer minNumberOfCharacters;
    private String defaultRemark;
}
