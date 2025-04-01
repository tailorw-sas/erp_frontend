package com.kynsoft.finamer.payment.infrastructure.identity.projection;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class HotelProjection {
    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean applyByTradingCompany;

    public HotelProjection(UUID id, String code, String name, String status, Boolean applyByTradingCompany) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
        this.applyByTradingCompany = applyByTradingCompany;
    }
    // getters...
}