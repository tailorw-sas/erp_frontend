package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ImportInnsistRoomRateKafka implements Serializable {
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double invoiceAmount;//Amount
    private String roomNumber;
    private Integer adults;
    private Integer children;
    private Double rateAdult;
    private Double rateChild;
    private Double hotelAmount;
    private String remark;
    private Long nights;
}
