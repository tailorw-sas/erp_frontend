package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTransactionTypeCommand implements ICommand {

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

    public CreateManageInvoiceTransactionTypeCommand(String code, String description, Status status, String name, Boolean isAgencyRateAmount, Boolean isNegative, Boolean isPolicyCredit, Boolean isRemarkRequired, Integer minNumberOfCharacters, String defaultRemark, boolean defaults) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.isAgencyRateAmount = isAgencyRateAmount;
        this.isNegative = isNegative;
        this.isPolicyCredit = isPolicyCredit;
        this.isRemarkRequired = isRemarkRequired;
        this.minNumberOfCharacters = minNumberOfCharacters;
        this.defaultRemark = defaultRemark;
        this.defaults = defaults;
    }

    public static CreateManageInvoiceTransactionTypeCommand fromRequest(CreateManageInvoiceTransactionTypeRequest request){
        return new CreateManageInvoiceTransactionTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getIsAgencyRateAmount(),
                request.getIsNegative(),
                request.getIsPolicyCredit(),
                request.getIsRemarkRequired(),
                request.getMinNumberOfCharacters(),
                request.getDefaultRemark(),
                request.isDefaults()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTransactionTypeMessage(id);
    }
}
