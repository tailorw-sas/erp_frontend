package com.kynsof.share.core.domain.kafka.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageEmployeeKafka implements Serializable {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneExtension;
    private List<UUID> manageAgencyList;
    private List<UUID> manageHotelList;
}
