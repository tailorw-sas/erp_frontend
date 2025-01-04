package com.kynsoft.finamer.audit.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorField {
    private String field;
    private String message;
}