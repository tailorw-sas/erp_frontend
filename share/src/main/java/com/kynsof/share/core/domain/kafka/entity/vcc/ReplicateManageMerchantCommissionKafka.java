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
public class ReplicateManageMerchantCommissionKafka {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("managerMerchant")
    private UUID managerMerchant;
    @JsonProperty("manageCreditCartType")
    private UUID manageCreditCartType;
    @JsonProperty("commission")
    private Double commission;
    @JsonProperty("calculationType")
    private String calculationType;
}
