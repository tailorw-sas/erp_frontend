package com.kynsof.share.core.domain.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageRatePlanKafka implements Serializable {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;

}
