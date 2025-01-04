package com.kynsoft.finamer.invoicing.domain.excel;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImportBookingRequest {

    private String importProcessId;
    private byte [] file;
    private EImportType importType;
    private String employee;
}
