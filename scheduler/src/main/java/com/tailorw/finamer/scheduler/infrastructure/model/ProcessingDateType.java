package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.ProcessingDateTypeDto;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "processing_date_type")
public class ProcessingDateType {
    @Id
    private UUID id;
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    public ProcessingDateType(ProcessingDateTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
    }

    public ProcessingDateTypeDto toAggregate(){
        return new ProcessingDateTypeDto(id, code, status);
    }
}
