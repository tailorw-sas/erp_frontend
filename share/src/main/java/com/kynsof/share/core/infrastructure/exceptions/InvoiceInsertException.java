package com.kynsof.share.core.infrastructure.exceptions;

public class InvoiceInsertException extends RuntimeException{

    public InvoiceInsertException(String message, Throwable cause){
        super(message, cause);
    }
}
