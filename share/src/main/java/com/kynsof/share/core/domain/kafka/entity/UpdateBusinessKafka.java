package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateBusinessKafka implements Serializable {
    private UUID id;
    private String name;
    private String latitude;
    private String longitude;
    private String address;
    private String logo;

    public UpdateBusinessKafka() {
    }

}
