package com.kynsoft.finamer.settings.application.command.manageInvoiceTransactionType.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageInvoiceTransactionTypeCommand implements ICommand {

    private UUID id;
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
    private boolean cloneAdjustmentDefault;

    public static UpdateManageInvoiceTransactionTypeCommand fromRequest(UpdateManageInvoiceTransactionTypeRequest request, UUID id){
        return new UpdateManageInvoiceTransactionTypeCommand(
                id,
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getIsAgencyRateAmount(),
                request.getIsNegative(),
                request.getIsPolicyCredit(),
                request.getIsRemarkRequired(),
                request.getMinNumberOfCharacters(),
                request.getDefaultRemark(),
                request.isDefaults(),
                request.isCloneAdjustmentDefault()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageInvoiceTransactionTypeMessage(id);
    }
}
