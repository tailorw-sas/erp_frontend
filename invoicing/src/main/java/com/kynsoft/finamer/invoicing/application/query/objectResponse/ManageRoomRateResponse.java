package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.*;

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
public class ManageRoomRateResponse implements IResponse {
    private UUID id;
    private Long room_rate_id;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double invoiceAmount;
    private String roomNumber;
    private Integer adults;
    private Integer children;
    private Double rateAdult;
    private Double rateChild;
    private Double hotelAmount;
    private String remark;
    private ManageBookingDto booking;

    public ManageRoomRateResponse(ManageRoomRateDto dto) {
        this.id = dto.getId();
        this.room_rate_id = dto.getRoom_rate_id();
        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.roomNumber = dto.getRoomNumber();
        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.rateAdult = dto.getRateAdult();
        this.rateChild = dto.getRateChild();
        this.hotelAmount = dto.getHotelAmount();
        this.remark = dto.getRemark();
        this.booking = dto.getBooking();
    }
}
