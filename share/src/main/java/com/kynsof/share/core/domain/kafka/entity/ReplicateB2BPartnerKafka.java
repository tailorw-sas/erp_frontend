package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplicateB2BPartnerKafka implements Serializable {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String password;
    private String ip;
    private String token;
    private String url;
    private String userName;
    private String status;
    private UUID b2BPartnerTypeDto;
}
