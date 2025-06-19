package com.kynsoft.finamer.payment.domain.dtoEnum;

import lombok.Getter;

@Getter
public enum EInvoiceType {
    INVOICE("INV"),
    INCOME("INC"),
    CREDIT("CRE"),
    OLD_CREDIT("OLD_CRE");

    private final String code;

    EInvoiceType(String code){
        this.code = code;
    }

    public static EInvoiceType fromName(String name){
        try {
            return EInvoiceType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No EInvoiceType was found with the name: " + name);
        }
    }
}
