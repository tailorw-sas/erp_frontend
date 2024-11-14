package com.kynsoft.finamer.creditcard.application.query.transaction.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class TransactionResume implements IResponse {
    private PaginatedResponse transactionSearchResponse;
    private TransactionTotalResume transactionTotalResume;
}
