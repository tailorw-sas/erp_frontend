package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentHistoryResponse implements IResponse {

    private UUID id;
    private Long paymentId;
    private String reference;
    private LocalDate transactionDate;

    public PaymentHistoryResponse(PaymentDto dto) {
        this.id = dto.getId();
        this.paymentId = dto.getPaymentId();
        this.reference = dto.getReference();
        this.transactionDate = dto.getTransactionDate();
    }

}
