package com.kynsoft.finamer.invoicing.application.query.report;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.ByteArrayOutputStream;

@AllArgsConstructor
@Getter
public class InvoiceReportResponse implements IResponse {
    private String FileName;
    private ByteArrayOutputStream file;
}
