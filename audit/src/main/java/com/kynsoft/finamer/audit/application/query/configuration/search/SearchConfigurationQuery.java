package com.kynsoft.finamer.audit.application.query.configuration.search;

import com.kynsoft.finamer.audit.domain.bus.query.IQuery;
import com.kynsoft.finamer.audit.domain.request.FilterCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchConfigurationQuery implements IQuery {
    private Pageable pageable;
    private List<FilterCriteria> filter;
    private String query;
}
