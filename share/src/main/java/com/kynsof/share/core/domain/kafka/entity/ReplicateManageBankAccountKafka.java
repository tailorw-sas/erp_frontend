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
public class ReplicateManageBankAccountKafka implements Serializable {
    @JsonProperty("id")
    private UUID id;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("status")
    private String status;
    @JsonProperty("nameOfBank")
    private String nameOfBank;
    @JsonProperty("manageHotel")
    private UUID manageHotel;

}
