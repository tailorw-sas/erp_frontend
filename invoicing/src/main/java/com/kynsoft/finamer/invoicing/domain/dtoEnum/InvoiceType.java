package com.kynsoft.finamer.invoicing.domain.dtoEnum;

public class InvoiceType {

    public static String getInvoiceTypeCode(EInvoiceType type) {

        switch (type) {
            case INVOICE:
                return "INV";

            case CREDIT:
                return "CRD";
            case INCOME:
                return "INC";

            case OLD_CREDIT:
                return "OLD";

            default:
                return "INV";
        }
    }

}
