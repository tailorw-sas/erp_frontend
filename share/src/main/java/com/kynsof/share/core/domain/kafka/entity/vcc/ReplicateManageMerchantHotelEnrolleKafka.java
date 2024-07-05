package com.kynsof.share.core.domain.kafka.entity.vcc;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("managerMerchant")
    private UUID managerMerchant;
    @JsonProperty("managerHotel")
    private UUID managerHotel;
    @JsonProperty("enrrolle")
    private String enrrolle;
}
