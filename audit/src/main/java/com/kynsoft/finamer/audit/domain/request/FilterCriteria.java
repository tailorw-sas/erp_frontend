package com.kynsoft.finamer.audit.domain.request;

import com.kynsoft.finamer.audit.infrastructure.specifications.LogicalOperation;
import com.kynsoft.finamer.audit.infrastructure.specifications.SearchOperation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FilterCriteria implements Serializable {
    private String key;
    private SearchOperation operator;
    private Object value;
    private LogicalOperation logicalOperation = LogicalOperation.AND;
}
