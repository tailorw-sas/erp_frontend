package com.kynsoft.finamer.creditcard.application.command.transaction.refund;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateRefundTransactionCommand implements ICommand {

    private Long id;
    private Long parentId;
    private Boolean hasCommission;
    private Double amount;
    private String employee;
    private UUID employeeId;

    public CreateRefundTransactionCommand(Long parentId, Boolean hasCommission, Double amount, String employee, UUID employeeId) {
        this.parentId = parentId;
        this.hasCommission = hasCommission;
        this.amount = amount;
        this.employee = employee;
        this.employeeId = employeeId;
    }

    public static CreateRefundTransactionCommand fromRequest(CreateRefundTransactionRequest request){
        return new CreateRefundTransactionCommand(
                request.getParentId(), request.getHasCommission(), request.getAmount(), request.getEmployee(), request.getEmployeeId()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateRefundTransactionMessage(id);
    }
}
