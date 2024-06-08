package com.kynsof.share.core.domain.bus.query;

public final class QueryNotRegisteredError extends Exception {
    public QueryNotRegisteredError(Class<? extends IQuery> query) {
        super(String.format("The query <%s> hasn't a query handler associated", query.toString()));
    }
}
