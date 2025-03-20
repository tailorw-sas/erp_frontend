package com.kynsoft.finamer.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageCountryDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Boolean isDefault;
    private String status;
    private ManageLanguageDto managerLanguage;
    private String iso3;
}
