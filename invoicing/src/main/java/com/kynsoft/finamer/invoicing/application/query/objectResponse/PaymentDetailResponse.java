package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.PaymentDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDetailResponse implements IResponse {

    private UUID id;
    private Long paymentDetailId;

    public PaymentDetailResponse(PaymentDetailDto dto) {
        this.id = dto.getId();
        this.paymentDetailId = dto.getPaymentDetailId();
    }

}
