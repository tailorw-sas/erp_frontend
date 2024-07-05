package com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.searchForPayment;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
public class GetManagePaymentTransactionTypeForPaymentQuery implements IQuery {

    private Pageable pageable;
    private Boolean applyDeposit;
    private Boolean deposit;
    private Boolean defaults;
}
