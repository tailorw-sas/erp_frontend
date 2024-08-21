package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "attachment_type")
public class AttachmentType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;
    private String name;
    private String description;
    private Boolean defaults;
    @Column(name = "anti_import",columnDefinition = "boolean default false")
    private boolean antiToIncomeImport;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public AttachmentType(AttachmentTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.defaults = dto.getDefaults();
        this.status = dto.getStatus();
        this.antiToIncomeImport=dto.isAntiToIncomeImport();
    }

    public AttachmentTypeDto toAggregate() {
        return new AttachmentTypeDto(id, code, name, description, defaults, status,antiToIncomeImport);
    }

}
