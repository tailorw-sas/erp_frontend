package com.kynsoft.finamer.payment.application.command.paymentImport.detail;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentImportDetailMessage implements ICommandMessage {
    private List<UUID> paymentId;
}
