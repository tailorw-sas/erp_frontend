package com.kynsoft.finamer.settings.application.command.manageVCCTransactionType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageVCCTransactionTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private Status status;
    private String name;
    private String description;
    private Boolean isActive;
    private Boolean negative;
    private Boolean isDefault;
    private Boolean subcategory;
    private Boolean onlyApplyNet;
    private Boolean policyCredit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;
    private String defaultRemark;
    private boolean manual;

    public CreateManageVCCTransactionTypeCommand(String code, Status status, String name, String description, Boolean isActive,
                                                 Boolean negative,
                                                 Boolean isDefault,
                                                 Boolean subcategory,
                                                 Boolean onlyApplyNet,
                                                 Boolean policyCredit,
                                                 Boolean remarkRequired,
                                                 Integer minNumberOfCharacter,
                                                 String defaultRemark,
                                                 boolean manual) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.status = status;
        this.name = name;
        this.description = description;
        this.isActive = isActive;
        this.negative = negative;
        this.isDefault = isDefault;
        this.onlyApplyNet = onlyApplyNet;
        this.policyCredit = policyCredit;
        this.remarkRequired = remarkRequired;
        this.subcategory = subcategory;
        this.minNumberOfCharacter = minNumberOfCharacter;
        this.defaultRemark = defaultRemark;
        this.manual = manual;
    }

    public static CreateManageVCCTransactionTypeCommand fromRequest(
            CreateManageVCCTransactionTypeRequest request) {
        return new CreateManageVCCTransactionTypeCommand(
                request.getCode(),
                request.getStatus(),
                request.getName(),
                request.getDescription(),
                request.getIsActive(),
                request.getNegative(),
                request.getIsDefault(),
                request.getSubcategory(),
                request.getOnlyApplyNet(),
                request.getPolicyCredit(),
                request.getRemarkRequired(),
                request.getMinNumberOfCharacter(),
                request.getDefaultRemark(),
                request.isManual()

        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageVCCTransactionTypeMessage(id);
    }
}
