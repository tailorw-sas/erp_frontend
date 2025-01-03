package com.kynsof.share.core.domain.http.entity;

import com.kynsof.share.core.domain.bus.query.IResponse;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingHttp implements IResponse, Serializable{
    private UUID id;
    private Long bookingId;
    private String reservationNumber;
    private String checkIn;
    private String checkOut;
    //private LocalDateTime checkIn;
    //private LocalDateTime checkOut;

    private String fullName;
    private String firstName;
    private String lastName;
    private Double invoiceAmount;
    private Double amountBalance;//dueAmount
    private String couponNumber;
    private Integer adults;
    private Integer children;
    private UUID bookingParent;
    private InvoiceHttp invoice;
    private String bookingDate;
    //private LocalDateTime bookingDate;
}
