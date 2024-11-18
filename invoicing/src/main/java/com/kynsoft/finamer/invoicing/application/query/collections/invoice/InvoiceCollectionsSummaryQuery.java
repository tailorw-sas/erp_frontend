package com.kynsoft.finamer.invoicing.application.query.collections.invoice;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class InvoiceCollectionsSummaryQuery implements IQuery {
    private List<FilterCriteria> filter;
    private Pageable pageable;
}
