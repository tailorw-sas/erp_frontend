package com.kynsof.share.core.domain.kafka.entity.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateManagePaymentSourceKafka implements Serializable {

    @JsonProperty("id")
    private UUID id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("status")
    private String status;
    private Boolean expense;
}
