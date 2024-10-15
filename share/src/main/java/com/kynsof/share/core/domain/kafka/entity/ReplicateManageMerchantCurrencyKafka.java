package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageMerchantCurrencyKafka implements Serializable {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private String value;
    private String description;
    private String status;
}
