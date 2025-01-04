package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageMerchantBankAccountKafka implements Serializable {

    private UUID id;
    private UUID managerMerchant;
    private UUID manageBank;
    private String accountNumber;
    private String description;
    private String status;
    private Set<UUID> creditCardTypes;
}
