package com.kynsof.share.core.domain.kafka.entity.update;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateManageNightTypeKafka implements Serializable {

    private UUID id;
    private String name;
}
