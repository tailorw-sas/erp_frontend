package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ParameterizationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "parameterization")
public class Parameterization implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private Boolean isActive;

    private String sent;

    private String reconciled;

    private String processed;

    private String canceled;

    private String pending;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Parameterization(ParameterizationDto dto){
        this.id = dto.getId();
        this.isActive = dto.getIsActive();
        this.sent = dto.getSent();
        this.reconciled = dto.getReconciled();
        this.processed = dto.getProcessed();
        this.canceled = dto.getCanceled();
        this.pending = dto.getPending();
    }

    public ParameterizationDto toAggregate(){
        return new ParameterizationDto(
                id, isActive, sent, reconciled, processed, canceled, pending
        );
    }

}
