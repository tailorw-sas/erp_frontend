package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageHotel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class ReplicateManageHotelKafka implements Serializable {
    private UUID id;
    private String code;
    private String name;
    private String roomType;
    private UUID tradingCompanyId;
}
