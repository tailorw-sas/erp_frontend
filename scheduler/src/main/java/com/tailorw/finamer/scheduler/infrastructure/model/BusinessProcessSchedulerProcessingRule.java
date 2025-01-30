package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerProcessingRuleDto;
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
@Table(name="business_process_scheduler_processing_rules")
public class BusinessProcessSchedulerProcessingRule {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "frequency_id", nullable = false)
    private Frequency frequency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processing_date_type_id", nullable = false)
    private ProcessingDateType processingDateType;

    @Column(name = "enable_processing_date_value")
    private Boolean enableProcessingDateValue;

    @Column(name = "enable_processing_date")
    private Boolean enableProcessingDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    public BusinessProcessSchedulerProcessingRule(BusinessProcessSchedulerProcessingRuleDto dto){
        this.id = dto.getId();
        this.frequency = Objects.nonNull(dto.getFrequency()) ? new Frequency(dto.getFrequency()) : null;
        this.processingDateType = Objects.nonNull(dto.getProcessingDateType()) ? new ProcessingDateType(dto.getProcessingDateType()) : null;
        this.enableProcessingDateValue = dto.getEnableProcessingDateValue();
        this.enableProcessingDate = dto.getEnableProcessingDate();
        this.status = dto.getStatus();
    }

    public BusinessProcessSchedulerProcessingRuleDto toAggregate(){
        return new BusinessProcessSchedulerProcessingRuleDto(
                id,
                Objects.nonNull(frequency) ? frequency.toAggregate() : null,
                Objects.nonNull(processingDateType) ? processingDateType.toAggregate() : null,
                enableProcessingDateValue,
                enableProcessingDate,
                status
        );
    }
}
