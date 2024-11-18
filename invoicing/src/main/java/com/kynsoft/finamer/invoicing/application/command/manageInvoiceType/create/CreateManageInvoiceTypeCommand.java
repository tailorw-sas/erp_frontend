package com.kynsoft.finamer.invoicing.application.command.manageInvoiceType.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageInvoiceTypeCommand implements ICommand {

    private UUID id;
    private String code;
    private String name;
    private boolean invoice;
    private boolean credit;
    private boolean income;
    private Status status;

    public CreateManageInvoiceTypeCommand(UUID id, String code, String name, boolean invoice, boolean credit, boolean income, Status status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.invoice = invoice;
        this.credit = credit;
        this.income = income;
        this.status = status;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageInvoiceTypeMessage(id);
    }
}
