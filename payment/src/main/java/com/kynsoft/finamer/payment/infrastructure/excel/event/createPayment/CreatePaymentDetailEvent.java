package com.kynsoft.finamer.payment.infrastructure.excel.event.createPayment;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;
@Getter
@Setter
public class CreatePaymentDetailEvent extends ApplicationEvent {

    private UUID id;
    private UUID employee;
    private Status status;
    private UUID payment;
    private UUID transactionType;
    private Double amount;
    private String remark;
    public CreatePaymentDetailEvent(Object source) {
        super(source);
    }
}
