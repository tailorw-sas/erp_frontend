package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.PaymentSourceProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePaymentSourceSearchResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;

    public ManagePaymentSourceSearchResponse(ManagePaymentSourceDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }

    public ManagePaymentSourceSearchResponse(PaymentSourceProjection paymentSource) {
        this.id = paymentSource.getId();
        this.code = paymentSource.getCode();
        this.name = paymentSource.getName();
        //this.status = paymentSource.getStatus();
    }
}
