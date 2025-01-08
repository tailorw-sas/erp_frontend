package com.kynsoft.finamer.insis.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplicateManageHotelKafka implements Serializable {

    private UUID id;
    private String code;
    private String name;
    private String roomType;
    private UUID tradingCompanyId;
}
