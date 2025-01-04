package com.kynsoft.finamer.creditcard.application.query.transaction.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindTransactionByUuidQuery implements IQuery {

    private final UUID transactionUuid;
}
