package com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagePaymentTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;

    private Boolean cash;
    private Boolean agencyRateAmount;
    private Boolean negative;
    private Boolean policyCredit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;
    private String defaultRemark;

    public CreateManagePaymentTransactionTypeCommand(String code, Status status, String name, String description, Boolean cash, Boolean agencyRateAmount, Boolean negative, Boolean policyCredit, Boolean remarkRequired, Integer minNumberOfCharacter, String defaultRemark) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
        this.cash = cash;
        this.agencyRateAmount = agencyRateAmount;
        this.negative = negative;
        this.policyCredit = policyCredit;
        this.remarkRequired = remarkRequired;
        this.minNumberOfCharacter = minNumberOfCharacter;
        this.defaultRemark = defaultRemark;

    }

    public static CreateManagePaymentTransactionTypeCommand fromRequest(CreateManagePaymentTransactionTypeRequest request) {
        return new CreateManagePaymentTransactionTypeCommand(request.getCode(),

                request.getStatus(), request.getName(), request.getDescription(), request.getCash(), request.getAgencyRateAmount(), request.getNegative(), request.getPolicyCredit(), request.getRemarkRequired(), request.getMinNumberOfCharacter(), request.getDefaultRemark());
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagePaymentTransactionTypeMessage(id);
    }
}
