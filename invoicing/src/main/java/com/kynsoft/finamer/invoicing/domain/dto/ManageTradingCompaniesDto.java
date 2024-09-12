package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTradingCompaniesDto {

    private UUID id;
    private String code;
    private Boolean isApplyInvoice;
    private Long autogen_code;
    private String cif;
    private String address;
    private String company;

}
