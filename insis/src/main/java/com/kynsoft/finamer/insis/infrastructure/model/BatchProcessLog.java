package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.BatchProcessLogDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import jakarta.persistence.*;
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
@Entity
@Table(name = "log", schema = "batch")
public class BatchProcessLog {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private BatchType type;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    private LocalDateTime startedAt;

    @Column(nullable = true)
    private LocalDateTime completedAt;
    private String hotel;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalRecordsRead;
    private int totalRecordsProcessed;
    private UUID processId;

    public BatchProcessLog(BatchProcessLogDto dto){
        this.id = dto.getId();
        this.type = dto.getType();
        this.status = dto.getStatus();
        this.startedAt = dto.getStartedAt();
        this.completedAt = dto.getCompletedAt();
        this.hotel = dto.getHotel();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.totalRecordsRead = dto.getTotalRecordsRead();
        this.totalRecordsProcessed = dto.getTotalRecordsProcessed();
        this.processId = dto.getProcessId();
    }

    public BatchProcessLogDto toAggregate(){
        return new BatchProcessLogDto(
                this.id,
                this.type,
                this.status,
                this.startedAt,
                this.completedAt,
                this.hotel,
                this.startDate,
                this.endDate,
                this.totalRecordsRead,
                this.totalRecordsProcessed,
                this.processId
        );
    }
}
