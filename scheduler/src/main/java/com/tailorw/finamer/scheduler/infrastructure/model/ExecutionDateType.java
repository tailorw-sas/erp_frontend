package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.ExecutionDateTypeDto;
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
@Table(name = "execution_date_type")
public class ExecutionDateType {

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

    public ExecutionDateType(ExecutionDateTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
    }

    public ExecutionDateTypeDto toAggregate(){
        return new ExecutionDateTypeDto(id, code, status);
    }
}
