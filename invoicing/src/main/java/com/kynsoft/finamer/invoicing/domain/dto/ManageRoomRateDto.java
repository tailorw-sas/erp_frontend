package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageRoomRateDto {
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
    private List<ManageAdjustmentDto> adjustments;
    private Long nights;

    public ManageRoomRateDto(ManageRoomRateDto dto) {
        this.id = UUID.randomUUID();
        this.roomRateId = dto.getRoomRateId();
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
        this.adjustments = new ArrayList<>();
        this.nights = dto.getNights();
    }
}
