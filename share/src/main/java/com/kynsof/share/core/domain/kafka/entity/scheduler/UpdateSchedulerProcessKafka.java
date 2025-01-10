package com.kynsof.share.core.domain.kafka.entity.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSchedulerProcessKafka {

    private UUID processId;
    private LocalDateTime completedAt;
    private String additionalDetails;
}
