package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class HotelPaymentBasicResponse {
    private UUID id;
    private Long hotelPaymentId;

    public HotelPaymentBasicResponse(HotelPaymentDto dto) {
        this.id = dto.getId();
        this.hotelPaymentId = dto.getHotelPaymentId();
    }
}
