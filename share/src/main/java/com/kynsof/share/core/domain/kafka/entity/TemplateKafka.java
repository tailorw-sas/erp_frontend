package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateKafka implements Serializable {

    private UUID id;
    private String templateCode;
    private String name;
    private String description;
    private UUID mailjetConfigurationDto;
    private LocalDateTime createdAt;
    private String languageCode;
    private String type;
}
