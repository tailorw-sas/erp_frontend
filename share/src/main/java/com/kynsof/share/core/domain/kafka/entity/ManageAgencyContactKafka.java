package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageAgencyContactKafka implements Serializable {

    private UUID id;
    private UUID manageAgency;
    private UUID manageRegion;
    private List<UUID> manageHotel;
    private String emailContact;
}
