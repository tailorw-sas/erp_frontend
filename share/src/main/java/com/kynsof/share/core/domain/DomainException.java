package com.kynsof.share.core.domain;

import java.io.Serializable;

public class DomainException extends RuntimeException implements Serializable {

    public DomainException(final String message) {
        super(message);
    }
}
