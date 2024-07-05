package com.kynsof.share.core.domain.kafka.entity.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateManageInvoiceTypeKafka implements Serializable {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("name")
    private String name;
}
