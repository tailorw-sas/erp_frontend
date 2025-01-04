package com.kynsoft.finamer.creditcard.application.command.hotelPayment.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateHotelPaymentCommand implements ICommand {

    private UUID id;
    private UUID manageBankAccount;
    private String remark;
    private UUID status;
    private Long hotelPaymentId;
    private String employee;
    private UUID employeeId;

    public UpdateHotelPaymentCommand(UUID id, UUID manageBankAccount, String remark, UUID status, String employee, UUID employeeId) {
        this.id = id;
        this.manageBankAccount = manageBankAccount;
        this.remark = remark;
        this.status = status;
        this.employee = employee;
        this.employeeId = employeeId;
    }

    public static UpdateHotelPaymentCommand fromRequest(UUID id, UpdateHotelPaymentRequest request) {
        return new UpdateHotelPaymentCommand(id, request.getManageBankAccount(), request.getRemark(), request.getStatus(), request.getEmployee(), request.getEmployeeId());
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateHotelPaymentMessage(id, hotelPaymentId);
    }
}
