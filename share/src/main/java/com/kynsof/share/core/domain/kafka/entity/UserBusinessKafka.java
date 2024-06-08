package com.kynsof.share.core.domain.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserBusinessKafka implements Serializable {
    @JsonProperty("idUser")
    private UUID idUser;
    @JsonProperty("idBusiness")
    private UUID idBusiness;

    public UserBusinessKafka() {
    }

}
