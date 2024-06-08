package com.kynsof.share.core.domain.kafka.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleKafka {
    private UUID id;
    private String name;
    private String description;
}