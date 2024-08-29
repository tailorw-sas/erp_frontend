package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import jakarta.persistence.*;
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
@Entity
@Table(name = "manage_booking")
public class ManageBooking {

    @Id
    @Column(name = "id")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_invoice", nullable = true)
    private ManageInvoice invoice;

    public ManageBooking(ManageBookingDto dto) {
        this.id = dto.getId();
        this.bookingId = dto.getBookingId();
        this.reservationNumber = dto.getReservationNumber();
        this.checkIn = dto.getCheckIn();
        this.checkOut = dto.getCheckOut();
        this.fullName = dto.getFullName();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.amountBalance = dto.getAmountBalance();
        this.couponNumber = dto.getCouponNumber();
        this.adults = dto.getAdults();
        this.children = dto.getChildren();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.invoice = dto.getInvoice() != null ? new ManageInvoice(dto.getInvoice()) : null;
    }

    public ManageBookingDto toAggregate(){
        return new ManageBookingDto(
                id, 
                bookingId,
                reservationNumber,
                checkIn,
                checkOut,
                fullName, 
                firstName, 
                lastName,
                invoiceAmount,
                amountBalance,
                couponNumber,
                adults,
                children,
                invoice != null ? invoice.toAggregateSample() : null
        );
    }

    public ManageBookingDto toAggregateSimple(){
        return new ManageBookingDto(
                id, 
                bookingId,
                reservationNumber,
                checkIn,
                checkOut,
                fullName, 
                firstName, 
                lastName,
                invoiceAmount,
                amountBalance,
                couponNumber,
                adults,
                children,
                null
        );
    }

}
