package com.kynsoft.finamer.payment.application.query.report;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.ByteArrayOutputStream;

@AllArgsConstructor
@Getter
public class PaymentReportResponse implements IResponse {
    private String FileName;
    private ByteArrayOutputStream file;
}
