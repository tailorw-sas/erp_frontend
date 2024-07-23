package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;

@Getter
@Setter
public class ExportInvoiceResponse implements IResponse {

    private ByteArrayOutputStream stream;

    public ExportInvoiceResponse(){
        stream = new ByteArrayOutputStream();
    }
}
