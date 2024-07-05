package com.kynsof.share.core.domain.kafka.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManageNightTypeKafka implements Serializable {

    private UUID id;
    private String code;
    private String name;
}
