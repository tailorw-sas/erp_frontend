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
@Table(name = "batch_log")
public class BatchProcessLog {

    @Id
    private UUID id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private BatchType type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BatchStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at", nullable = true)
    private LocalDateTime completedAt;

    private String hotel;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_records_read")
    private int totalRecordsRead;

    @Column(name = "total_records_processed")
    private int totalRecordsProcessed;

    @Column(name = "process_id")
    private UUID processId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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
        this.errorMessage = dto.getErrorMessage();
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
                this.processId,
                this.errorMessage
        );
    }
}
