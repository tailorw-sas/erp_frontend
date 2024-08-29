package com.kynsoft.finamer.settings.application.command.manageHotel.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageHotelRequest {

    private String description;
    private Status status;
    private String name;
    private String babelCode;
    private UUID manageCountry;
    private UUID manageCityState;
    private String city;
    private String address;
    private UUID manageCurrency;
    private UUID manageRegion;
    private UUID manageTradingCompanies;
    private Boolean applyByTradingCompany;
    private String prefixToInvoice;
    private Boolean isVirtual;
    private Boolean requiresFlatRate;
    private Boolean isApplyByVCC;
    private Boolean autoApplyCredit;
}
