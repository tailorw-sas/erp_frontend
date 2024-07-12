package com.kynsof.share.core.domain.kafka.entity.update;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeePermissionKafka implements Serializable {
    private UUID employee;
    private List<UUID> permissionIds;
}
