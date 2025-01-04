package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ApplyPaymentCommand implements ICommand {

    private UUID payment;
    private boolean applyDeposit;
    private boolean applyPaymentBalance;
    private List<UUID> invoices;
    private List<UUID> deposits;
    private final IMediator mediator;
    private UUID employee;

    public ApplyPaymentCommand(UUID payment, boolean applyDeposit, boolean applyPaymentBalance, List<UUID> invoices, List<UUID> deposits, final IMediator mediator, UUID employee) {
        this.payment = payment;
        this.applyDeposit = applyDeposit;
        this.applyPaymentBalance = applyPaymentBalance;
        this.invoices = invoices;
        this.deposits = deposits;
        this.mediator = mediator;
        this.employee = employee;
    }

    public static ApplyPaymentCommand fromRequest(ApplyPaymentRequest request, final IMediator mediator) {
        return new ApplyPaymentCommand(
                request.getPayment(),
                request.isApplyDeposit(),
                request.isApplyPaymentBalance(),
                request.getInvoices(),
                request.getDeposits(),
                mediator,
                request.getEmployee()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new ApplyPaymentMessage();
    }
}
