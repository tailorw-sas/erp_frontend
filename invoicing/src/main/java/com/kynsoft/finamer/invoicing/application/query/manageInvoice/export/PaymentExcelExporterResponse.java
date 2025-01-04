package com.kynsoft.finamer.invoicing.application.query.manageInvoice.export;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentExcelExporterResponse implements IResponse {
    private byte[] excel;
    private String fileName;
}
