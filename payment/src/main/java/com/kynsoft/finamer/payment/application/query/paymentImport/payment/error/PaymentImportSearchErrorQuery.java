package com.kynsoft.finamer.payment.application.query.paymentImport.payment.error;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportSearchErrorQuery implements IQuery {

    private SearchRequest searchRequest;
}
