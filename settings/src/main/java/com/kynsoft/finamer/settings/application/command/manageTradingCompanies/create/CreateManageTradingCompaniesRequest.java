package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageTradingCompaniesRequest {

    private String code;
    private String description;
    private Status status;
    private String company;
    private Long cif;
    private String address;
    private UUID country;
    private UUID cityState;
    private String city;
    private Long zipCode;
    private String innsistCode;
    private Boolean isApplyInvoice;
}
