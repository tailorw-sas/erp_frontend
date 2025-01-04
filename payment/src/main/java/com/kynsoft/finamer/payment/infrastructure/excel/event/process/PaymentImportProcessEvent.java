package com.kynsoft.finamer.payment.infrastructure.excel.event.process;

import com.kynsoft.finamer.payment.domain.dto.PaymentImportStatusDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentImportProcessEvent extends ApplicationEvent {


    private final PaymentImportStatusDto paymentImportStatusDto;


    public PaymentImportProcessEvent(Object source, PaymentImportStatusDto paymentImportStatusDto) {
        super(source);
        this.paymentImportStatusDto = paymentImportStatusDto;
    }
}
