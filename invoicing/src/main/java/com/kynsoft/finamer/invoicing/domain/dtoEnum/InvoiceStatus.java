package com.kynsoft.finamer.invoicing.domain.dtoEnum;

public class InvoiceStatus {

    public static String getInvoiceStatusCode(EInvoiceStatus status){
        switch (status) {
            case PROCECSED:
                return "PRO";

            case RECONCILED:
                return "REC";
            case SENT:
                return "SENT";

            case CANCELED:
                return "CANC";
            case PENDING:
                return "PEND";

            default:
                return "INV";
        }
    }
}
