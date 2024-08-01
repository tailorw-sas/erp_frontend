package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentImportDetailCommand implements ICommand {
    private PaymentImportDetailRequest paymentImportDetailRequest;
    @Override
    public ICommandMessage getMessage() {
        return null;
    }
}
