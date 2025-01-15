package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageTradingCompanyKafka {

    private UUID id;
    private String code;
    private boolean isApplyInvoice;
    private String cif;
    private String address;
    private String company;
    private String status;

    private String description;
    private UUID country;
    private UUID cityState;
    private String city;
    private String zipCode;
    private String innsistCode;
}
