package com.kynsof.share.core.domain.kafka.entity.update;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateManageEmployeeKafka implements Serializable {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
