package com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportInnsistRoomRateRequest implements Serializable {
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
    private Long nights;
}
