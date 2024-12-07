package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentStatusHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HotelPaymentStatusHistoryResponse implements IResponse {

    private UUID id;
    private String description;
    private LocalDateTime createdAt;
    private String employee;
    private HotelPaymentBasicResponse hotelPayment;
    private ManagePaymentTransactionStatusResponse status;

    public HotelPaymentStatusHistoryResponse(HotelPaymentStatusHistoryDto dto) {
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt();
        this.employee = dto.getEmployee();
        this.hotelPayment = dto.getHotelPayment() != null ? new HotelPaymentBasicResponse(dto.getHotelPayment()) : null;
        this.status = dto.getStatus() != null ? new ManagePaymentTransactionStatusResponse(dto.getStatus()) : null;
    }
}
