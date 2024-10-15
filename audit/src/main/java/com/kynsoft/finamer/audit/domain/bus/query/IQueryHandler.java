package com.kynsoft.finamer.audit.domain.bus.query;

public interface IQueryHandler<Q extends IQuery, R extends IResponse> {
    R handle(Q query);
}
