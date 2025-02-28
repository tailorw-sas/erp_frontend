package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.HotelProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ManageHotelResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean applyByTradingCompany;
    private UUID manageTradingCompany;
    private Boolean autoApplyCredit;

    public ManageHotelResponse(ManageHotelDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.applyByTradingCompany = dto.getApplyByTradingCompany();
        this.manageTradingCompany = dto.getManageTradingCompany();
        this.autoApplyCredit = dto.getAutoApplyCredit();
    }

    public ManageHotelResponse(HotelProjection hotel) {
        this.id = hotel.getId();
        this.code = hotel.getCode();
        this.name = hotel.getName();
        this.status = hotel.getStatus();
        this.applyByTradingCompany = null;
        this.manageTradingCompany = null;
        this.autoApplyCredit = null;
    }
}
