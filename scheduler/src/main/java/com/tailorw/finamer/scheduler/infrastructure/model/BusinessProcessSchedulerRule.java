package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerRuleDto;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="business_process_scheduler_rules")
public class BusinessProcessSchedulerRule {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "frequency_id", nullable = false)
    private Frequency frequency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "interval_type_id", nullable = false)
    private IntervalType intervalType;

    @Column(name = "enable_interval")
    private Boolean enableInterval;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "execution_date_type_id", nullable = true)
    private ExecutionDateType executionDateType;

    @Column(name = "enable_execution_date_value")
    private Boolean enableExecutionDateValue;

    @Column(name = "enable_execution_date")
    private Boolean enableExecutionDate;

    @Column(name = "enable_execution_time")
    private Boolean enableExecutionTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    public BusinessProcessSchedulerRule(BusinessProcessSchedulerRuleDto dto){
        this.id = dto.getId();
        this.frequency = Objects.nonNull(dto.getFrequency()) ? new Frequency(dto.getFrequency()) : null;
        this.intervalType = Objects.nonNull(dto.getIntervalType()) ? new IntervalType(dto.getIntervalType()) : null;
        this.enableInterval = dto.getEnableInterval();
        this.executionDateType = Objects.nonNull(dto.getExecutionDateType()) ? new ExecutionDateType(dto.getExecutionDateType()) : null;
        this.enableExecutionDateValue = dto.getEnableExecutionDateValue();
        this.enableExecutionDate = dto.getEnableExecutionDate();
        this.enableExecutionTime = dto.getEnableExecutionTime();
        this.status = dto.getStatus();
    }

    public BusinessProcessSchedulerRuleDto toAggregate(){
        return new BusinessProcessSchedulerRuleDto(
                this.id,
                Objects.nonNull(this.frequency) ? this.frequency.toAggregate() : null,
                Objects.nonNull(this.intervalType) ? this.intervalType.toAggregate() : null,
                this.enableInterval,
                Objects.nonNull(this.executionDateType) ? this.executionDateType.toAggregate() : null,
                this.enableExecutionDateValue,
                this.enableExecutionDate,
                this.enableExecutionTime,
                this.status
        );
    }

}
