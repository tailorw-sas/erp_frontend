package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="business_process_scheduler")
public class BusinessProcessScheduler {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "frequency_id", nullable = false)
    private Frequency frequency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interval_type_id", nullable = false)
    private IntervalType intervalType;

    @Column(name = "interval_value")
    private Integer interval;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "execution_date_type_id", nullable = true)
    private ExecutionDateType executionDateType;

    @Column(name = "execution_date_value")
    private String executionDateValue;

    @Column(name = "execution_date")
    private LocalDate executionDate;

    @Column(name = "execution_time")
    private LocalTime executionTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processing_date_type_id", nullable = true)
    private ProcessingDateType processingDateType;

    @Column(name = "processing_date_value")
    private Integer processingDateValue;

    @Column(name = "processing_date")
    private LocalDate processingDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "params", columnDefinition = "TEXT")
    private String params;

    @Column(name = "last_execution_datetime")
    private LocalDateTime lastExecutionDatetime;

    @Column(name = "is_in_process", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isInProcess;

    @Column(name = "allows_queueing")
    private boolean allowsQueueing = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "process_id", nullable = false)
    private BusinessProcess process;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @Column(name = "start_time")
    public LocalTime startTime;

    @Column(name = "end_time")
    public LocalTime endTime;

    public BusinessProcessScheduler(BusinessProcessSchedulerDto dto){
        this.id = dto.getId();
        this.frequency = Objects.nonNull(dto.getFrequency()) ? new Frequency(dto.getFrequency()) : null;
        this.intervalType = Objects.nonNull(dto.getIntervalType()) ? new IntervalType(dto.getIntervalType()) : null;
        this.interval = dto.getInterval();
        this.executionDateType = Objects.nonNull(dto.getExecutionDateType()) ? new ExecutionDateType(dto.getExecutionDateType()) : null;
        this.executionDateValue = dto.getExecutionDateValue();
        this.executionDate = dto.getExecutionDate();
        this.executionTime = dto.getExecutionTime();
        this.processingDateType = Objects.nonNull(dto.getProcessingDateType()) ? new ProcessingDateType(dto.getProcessingDateType()) : null;
        this.processingDateValue = dto.getProcessingDateValue();
        this.processingDate = dto.getProcessingDate();
        this.status = dto.getStatus();
        this.params = dto.getParams();
        this.lastExecutionDatetime = dto.getLastExecutionDatetime();
        this.isInProcess = dto.isInProcess();
        this.process = Objects.nonNull(dto.getProcess()) ? new BusinessProcess(dto.getProcess()) : null;
        this.updatedAt = dto.getUpdatedAt();
        this.deletedAt = dto.getDeletedAt();
        this.allowsQueueing = dto.isAllowsQueueing();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
    }

    public BusinessProcessSchedulerDto toAggregate(){
        return new BusinessProcessSchedulerDto(
                id,
                Objects.nonNull(frequency) ? frequency.toAggregate() : null,
                Objects.nonNull(intervalType) ? intervalType.toAggregate() : null,
                interval,
                Objects.nonNull(executionDateType) ? executionDateType.toAggregate() : null,
                executionDateValue,
                executionDate,
                executionTime,
                Objects.nonNull(processingDateType) ? processingDateType.toAggregate() : null,
                processingDateValue,
                processingDate,
                status,
                params,
                lastExecutionDatetime,
                isInProcess,
                Objects.nonNull(process) ? process.toAgrregate() : null,
                updatedAt,
                deletedAt,
                allowsQueueing,
                startTime,
                endTime
        );
    }
}
