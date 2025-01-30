package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "log_scheduler")
public class SchedulerLog {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scheduler_id", nullable = false)
    private BusinessProcessScheduler scheduler;

    @Column(name = "log_date")
    private LocalDate dateLog;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "processing_date")
    private LocalDate processingDate;

    @Column(columnDefinition = "TEXT")
    private String details;

    public SchedulerLog(SchedulerLogDto dto){
        this.id = dto.getId();
        this.scheduler = Objects.nonNull(dto.getScheduler()) ? new BusinessProcessScheduler(dto.getScheduler()) : null;
        this.dateLog = dto.getDateLog();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.completedAt = dto.getCompletedAt();
        this.processingDate = dto.getProcessingDate();
        this.details = dto.getDetails();
    }

    public SchedulerLogDto toAggregate(){
        return new SchedulerLogDto(
                id,
                Objects.nonNull(scheduler) ? scheduler.toAggregate() : null,
                dateLog,
                status,
                updatedAt,
                completedAt,
                processingDate,
                details
        );
    }
}
