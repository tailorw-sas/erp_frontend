package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageHotelProjection {

    private UUID id;
    private String code;
    private Status status;
    private String description;
    private String name;
    private String babelCode;
    private ManageCountry manageCountry;
    private ManageCityState manageCityState;
    private String city;
    private String address;
    private ManagerCurrency manageCurrency;
    private ManageRegion manageRegion;
    private ManageTradingCompanies manageTradingCompanies;
    private Boolean applyByTradingCompany;
    private String prefixToInvoice;
    private Boolean isVirtual;
    private Boolean requiresFlatRate;
    private Boolean isApplyByVCC;
    private Boolean autoApplyCredit;

    public ManageHotelProjection(UUID id,
                                 String code,
                                 String name,
                                 Status status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }
}
