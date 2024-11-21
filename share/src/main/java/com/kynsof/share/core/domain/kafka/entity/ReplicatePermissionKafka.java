package com.kynsof.share.core.domain.kafka.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplicatePermissionKafka implements Serializable {

    private UUID id;
    private String code;
    private String description;
    private UUID module;
    private String status;
    private String action;
    private boolean deleted = false;
    private LocalDateTime createdAt;
    private Boolean isHighRisk;
    private Boolean isIT;
    private String name;
}
