package com.kynsoft.finamer.settings.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean enabledToPolicy;
    private boolean income;
    private boolean credit;
    private boolean invoice;

    public CreateManageInvoiceTypeCommand(String code, String description, Status status, String name, Boolean enabledToPolicy, boolean income, boolean credit, boolean invoice) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.description = description;
        this.status = status;
        this.name = name;
        this.enabledToPolicy = enabledToPolicy;
        this.income = income;
        this.credit = credit;
        this.invoice = invoice;
    }

    public static CreateManageInvoiceTypeCommand fromRequest(CreateManageInvoiceTypeRequest request){
        return new CreateManageInvoiceTypeCommand(
                request.getCode(),
                request.getDescription(),
                request.getStatus(),
                request.getName(),
                request.getEnabledToPolicy(),
                request.isIncome(),
                request.isCredit(),
                request.isInvoice()
        );

    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTypeMessage(id);
    }
}
