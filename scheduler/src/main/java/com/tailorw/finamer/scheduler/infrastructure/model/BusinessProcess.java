package com.tailorw.finamer.scheduler.infrastructure.model;

import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessDto;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="business_process")
public class BusinessProcess {

    @Id
    private UUID id;

    private String code;

    @Column(name = "process_name")
    private String processName;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<BusinessProcessScheduler> schedulers = new ArrayList<>();

    public BusinessProcess(BusinessProcessDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.processName = dto.getProcessName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.deletedAt = dto.getDeletedAt();
    }

    public BusinessProcessDto toAgrregate(){
        return new BusinessProcessDto(
                id,
                code,
                processName,
                description,
                status,
                updatedAt,
                deletedAt
        );
    }
}
