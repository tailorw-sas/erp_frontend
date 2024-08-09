package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@Getter
@Setter
public class PaymentImportDetailCommand implements ICommand {
    private final PaymentImportDetailRequest paymentImportDetailRequest;
    private List<UUID> paymentIds;

    public PaymentImportDetailCommand(PaymentImportDetailRequest paymentImportDetailRequest) {
        this.paymentImportDetailRequest = paymentImportDetailRequest;
    }

    @Override
    public ICommandMessage getMessage() {
        return new PaymentImportDetailMessage(paymentIds);
    }
}
