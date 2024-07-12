package com.kynsoft.finamer.invoicing.domain.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportBookingRequest {

    private String importProcessId;
    private byte [] file;
}
