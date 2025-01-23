package com.kynsoft.finamer.payment.infrastructure.excel.event.deposit;

import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class DepositEvent extends ApplicationEvent {

    private PaymentProjectionSimple paymentDto;
    private Double amount;
    private String remark;

    public DepositEvent(Object source) {
        super(source);
    }
}
