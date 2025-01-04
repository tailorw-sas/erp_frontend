package com.kynsoft.finamer.payment.application.query.collections.payment;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Builder
@Data
public class PaymentCollectionResponse implements IResponse {
    private PaginatedResponse paginatedResponse;
    private PaymentCollectionsSummaryResponse paymentCollectionsSummaryResponse;

}
