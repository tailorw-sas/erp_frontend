package com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendAccountStatement;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SendAccountStatementResponse implements IResponse {

    private String file;

}
