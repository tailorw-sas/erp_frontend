package com.kynsoft.finamer.insis.domain.dto;

import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImportProcessDto {

    private UUID id;
    private ImportProcessStatus status;
    private LocalDate importDate;
    private LocalDateTime completedAt;
    private int totalBookings;
    private UUID userId;
}
