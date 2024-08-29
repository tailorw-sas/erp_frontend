package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
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
public class ManageBookingResponse implements IResponse {
    private UUID id;
    private Long bookingId;
    private String reservationNumber;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private String fullName;
    private String firstName;
    private String lastName;
    private Double invoiceAmount;
    private Double amountBalance;//dueAmount
    private String couponNumber;
    private Integer adults;
    private Integer children;

    private ManageInvoiceResponse invoice;

    public ManageBookingResponse(ManageBookingDto dto) {
        this.id = dto.getId();
        this.bookingId = dto.getBookingId();
        this.reservationNumber = dto.getReservationNumber();
        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();
        this.fullName = dto.getFullName();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.amountBalance = dto.getAmountBalance();
        this.couponNumber = dto.getCouponNumber();
        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.invoice = dto.getInvoice() != null ? new ManageInvoiceResponse(dto.getInvoice()) : null;
    }

}
