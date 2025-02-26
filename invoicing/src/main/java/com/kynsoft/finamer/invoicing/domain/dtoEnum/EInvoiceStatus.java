package com.kynsoft.finamer.invoicing.domain.dtoEnum;

import lombok.Getter;

@Getter
public enum EInvoiceStatus {
    RECONCILED("REC" ),
    SENT("SEND" ),
    CANCELED("CAN" ),
    PENDING("PEND" ),
    PROCESSED("PROC" );

    private final String code;
    EInvoiceStatus(String code) {

        this.code = code;
    }

    public static EInvoiceStatus fromCode(String code) {
        for (EInvoiceStatus status : EInvoiceStatus.values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status code: " + code);
    }

    public static EInvoiceStatus fromName(String name) {
        try {
            return EInvoiceStatus.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status name: " + name, e);
        }
    }

}
