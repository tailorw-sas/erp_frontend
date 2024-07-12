package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreditCardCloseOperationResponse implements IResponse {

    private UUID id;
    private Status status;
    private ManageHotelResponse hotel;
    private LocalDate beginDate;
    private LocalDate endDate;

    public CreditCardCloseOperationResponse(CreditCardCloseOperationDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.beginDate = dto.getBeginDate();
        this.endDate = dto.getEndDate();
    }

}
