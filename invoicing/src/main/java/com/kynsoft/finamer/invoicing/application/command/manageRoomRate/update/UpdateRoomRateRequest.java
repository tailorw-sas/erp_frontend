package com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomRateRequest {

    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double invoiceAmount;
    private Integer adults;
    private Integer children;
    private Double hotelAmount;
}
