package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MerchantLanguageCodeDto {

    private UUID id;
    private String name;
    private String merchantLanguage;
    private ManageLanguageDto manageLanguage;
    private ManageMerchantDto manageMerchant;

}
