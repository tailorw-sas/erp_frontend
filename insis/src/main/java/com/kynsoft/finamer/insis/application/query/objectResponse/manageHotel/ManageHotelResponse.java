package com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ManageHotelResponse implements IResponse {
    private UUID id;
    private String code;
    private boolean deleted;
    private String name;
    private String status;
    private LocalDateTime updatedAt;
    private ManageTradingCompanyDto tradingCompany;

    public ManageHotelResponse(ManageHotelDto dto){
        this.id =dto.getId();
        this.code = dto.getCode();
        this.deleted = dto.isDeleted();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.tradingCompany = dto.getManageTradingCompany();
    }
}
