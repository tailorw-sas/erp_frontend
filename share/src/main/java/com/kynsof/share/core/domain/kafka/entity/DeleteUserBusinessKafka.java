package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DeleteUserBusinessKafka {
    private UUID userId;
    private UUID businessId;

    public DeleteUserBusinessKafka() {
    }

}
