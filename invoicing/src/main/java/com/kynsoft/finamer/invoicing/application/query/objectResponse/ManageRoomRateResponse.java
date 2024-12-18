package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import java.text.DecimalFormat;

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
    private Long roomRateId;
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
    private Long nights;

    public ManageRoomRateResponse(ManageRoomRateDto dto) {
        //DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        this.id = dto.getId();
        this.roomRateId = dto.getRoomRateId();
        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();
        
        this.invoiceAmount = dto.getInvoiceAmount() != null ? dto.getInvoiceAmount() : null;
        this.roomNumber = dto.getRoomNumber();
        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.rateAdult = dto.getRateAdult() != null ? dto.getRateAdult() : null;
        this.rateChild = dto.getRateChild() != null ? dto.getRateChild() : null;
        this.hotelAmount = dto.getHotelAmount() != null ? dto.getHotelAmount() : null;
        this.remark = dto.getRemark();
        this.booking = dto.getBooking();
        this.nights = dto.getNights();
    }
}
