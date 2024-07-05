package com.kynsof.share.core.domain.kafka.entity.vcc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageMerchantHotelEnrolleKafka {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerHotel;
    private String enrrolle;
}
