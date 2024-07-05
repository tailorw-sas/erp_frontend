package com.kynsoft.finamer.creditcard.application.query.transaction.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindTransactionByIdQuery implements IQuery {

    private final Long id;
}
