package com.tailorw.finamer.scheduler.domain.dto;

import com.tailorw.finamer.scheduler.infrastructure.model.enums.ProcessStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogRoomRatesBusinessProcessDto {
    private UUID id;
    private LocalDateTime logDate;
    private ProcessStatus status;
    private LocalDate processingDate;
    private String params;
    private LocalDateTime completedAt;
}
