package com.kynsoft.finamer.invoicing.application.query.collections;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class CollectionsSummaryQuery implements IQuery {
    private List<FilterCriteria> filter;
    private String query;
}
