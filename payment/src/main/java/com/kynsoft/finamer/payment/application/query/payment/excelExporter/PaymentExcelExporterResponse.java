package com.kynsoft.finamer.payment.application.query.payment.excelExporter;

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
}
