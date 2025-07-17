package com.kynsoft.finamer.insis.domain.dto;

import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BatchProcessLogDto {

    private UUID id;
    private BatchType type;
    private BatchStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalRecordsRead;
    private int totalRecordsProcessed;
    private UUID processId;
    private String errorMessage;
}
