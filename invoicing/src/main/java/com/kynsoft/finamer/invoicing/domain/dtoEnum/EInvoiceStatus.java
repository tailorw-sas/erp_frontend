package com.kynsoft.finamer.invoicing.domain.dtoEnum;

import lombok.Getter;

@Getter
public enum EInvoiceStatus {
    PROCECSED("PROC" ),
    RECONCILED("REC" ),
    SENT("SEND" ),
    CANCELED("CAN" ),
    PENDING("PEND" ),
    PROCESSED("PROC" );

    private final String code;
    EInvoiceStatus(String code) {

        this.code = code;
    }

}
