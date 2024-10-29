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
public class ManageInvoiceTypeDto {

    private UUID id;
    private String code;
    private String name;
    private boolean invoice;
    private boolean credit;
    private boolean income;
}
