package com.kynsoft.finamer.payment.application.query.collections.payment;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class PaymentCollectionsSummaryQuery implements IQuery {
    private List<FilterCriteria> filter;
    private Pageable  pageable;
}
