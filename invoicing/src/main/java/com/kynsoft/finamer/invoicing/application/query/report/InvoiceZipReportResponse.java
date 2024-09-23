package com.kynsoft.finamer.invoicing.application.query.report;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;

import java.util.List;

@AllArgsConstructor
@Getter
public class InvoiceZipReportResponse implements IResponse {
    private String FileName;
    private InputStreamResource file;
}
