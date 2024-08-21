package com.kynsoft.finamer.payment.infrastructure.excel.event.process;

import com.kynsoft.finamer.payment.domain.dto.PaymentImportStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.data.redis.core.index.Indexed;

@Getter
public class PaymentImportProcessEvent extends ApplicationEvent {


    private final PaymentImportStatusDto paymentImportStatusDto;


    public PaymentImportProcessEvent(Object source, PaymentImportStatusDto paymentImportStatusDto) {
        super(source);
        this.paymentImportStatusDto = paymentImportStatusDto;
    }
}
