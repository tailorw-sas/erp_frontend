package com.kynsof.share.core.infrastructure.specifications;

import java.io.Serializable;

public enum SearchOperation implements Serializable {
    LIKE,
    EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN_OR_EQUAL_TO,
    NOT_EQUALS,
    IN,
    NOT_IN,
    IS_NULL,
    IS_NOT_NULL,
    IS_TRUE,
    IS_FALSE
}
