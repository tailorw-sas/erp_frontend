package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

public class BaseXml    {
    protected String safe(String value) {
        return value != null ? value : "";
    }
}
