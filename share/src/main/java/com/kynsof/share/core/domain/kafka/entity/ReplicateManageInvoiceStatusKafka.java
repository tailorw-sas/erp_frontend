package com.kynsof.share.core.domain.kafka.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReplicateManageInvoiceStatusKafka implements Serializable {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("showClone")
    private Boolean showClone;
}
