package com.kynsof.share.core.infrastructure.specifications;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;
    private LogicalOperation logicalOperation = LogicalOperation.AND; // Valor por defecto

    public SearchCriteria(String key, SearchOperation operation, Object value,  LogicalOperation logicalOperation ) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.logicalOperation = logicalOperation;
    }
}
