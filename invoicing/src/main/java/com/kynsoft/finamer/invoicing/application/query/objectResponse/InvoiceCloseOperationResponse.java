package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
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
public class InvoiceCloseOperationResponse implements IResponse {

    private UUID id;
    private Status status;
    private ManageHotelResponse hotel;
    private LocalDate beginDate;
    private LocalDate endDate;

    public InvoiceCloseOperationResponse(InvoiceCloseOperationDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.beginDate = dto.getBeginDate();
        this.endDate = dto.getEndDate();
    }

}
