package com.kynsoft.finamer.invoicing.domain.dtoEnum;

public class InvoiceType {

    public static String getInvoiceTypeCode(EInvoiceType type) {

        return switch (type) {
            case CREDIT, OLD_CREDIT -> "CRE";
            case INCOME -> "INC";
            default -> "INV";
        };
    }

}
