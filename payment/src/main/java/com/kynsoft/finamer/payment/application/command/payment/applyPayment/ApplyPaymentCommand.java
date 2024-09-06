package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApplyPaymentCommand implements ICommand {

    private UUID payment;
    private List<UUID> invoices;
    private final IMediator mediator;

    public ApplyPaymentCommand(UUID payment, List<UUID> invoices, final IMediator mediator) {
        this.payment = payment;
        this.invoices = invoices;
        this.mediator = mediator;
    }

    public static ApplyPaymentCommand fromRequest(ApplyPaymentRequest request, final IMediator mediator) {
        return new ApplyPaymentCommand(
                request.getPayment(),
                request.getInvoices(),
                mediator
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new ApplyPaymentMessage();
    }
}
