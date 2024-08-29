package com.kynsoft.finamer.payment.domain.dto;

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
public class ManageBookingDto {
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
    private ManageInvoiceDto invoice;
}
