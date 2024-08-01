package com.kynsoft.finamer.payment.application.command.paymentImport.payment;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentImportCommand implements ICommand {
    private PaymentImportRequest paymentImportRequest;
    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
