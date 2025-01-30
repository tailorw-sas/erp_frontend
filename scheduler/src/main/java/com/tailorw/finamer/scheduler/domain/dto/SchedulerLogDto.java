package com.tailorw.finamer.scheduler.domain.dto;

import com.tailorw.finamer.scheduler.infrastructure.model.enums.ProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerLogDto {

    private UUID id;
    private BusinessProcessSchedulerDto scheduler;
    private LocalDate dateLog;
    private ProcessStatus status;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private LocalDate processingDate;
    private String details;
}
