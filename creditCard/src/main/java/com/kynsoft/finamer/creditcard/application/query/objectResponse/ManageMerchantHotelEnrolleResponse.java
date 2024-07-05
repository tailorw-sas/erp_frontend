package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageMerchantHotelEnrolleResponse implements IResponse {

    private UUID id;
    private ManageMerchantResponse managerMerchant;
    private ManageHotelResponse managerHotel;

    private String enrolle;
    public ManageMerchantHotelEnrolleResponse(ManageMerchantHotelEnrolleDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManageMerchant() != null ? new ManageMerchantResponse(dto.getManageMerchant()) : null;
        this.managerHotel = dto.getManageHotel() != null ? new ManageHotelResponse(dto.getManageHotel()) : null;
    }

}
