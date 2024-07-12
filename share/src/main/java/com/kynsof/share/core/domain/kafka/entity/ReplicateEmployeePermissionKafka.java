package com.kynsof.share.core.domain.kafka.entity;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateEmployeePermissionKafka implements Serializable {
    private UUID employee;
    private List<UUID> permissionIds;
}
