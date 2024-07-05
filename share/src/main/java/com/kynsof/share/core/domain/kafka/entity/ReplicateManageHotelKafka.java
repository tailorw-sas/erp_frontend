package com.kynsof.share.core.domain.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReplicateManageHotelKafka implements Serializable {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("isApplyByVCC")
    private Boolean isApplyByVCC;

    public ReplicateManageHotelKafka(UUID id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }
}
