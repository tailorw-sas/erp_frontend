package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_transaction_status")
public class ManagePaymentTransactionStatus implements Serializable {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean inProgress;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean completed;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean cancelled;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean applied;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean requireValidation;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_payment_transaction_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManagePaymentTransactionStatus> navigate = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ManagePaymentTransactionStatus(ManagePaymentTransactionStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.inProgress = dto.isInProgress();
        this.completed = dto.isCompleted();
        this.cancelled = dto.isCancelled();
        this.applied = dto.isApplied();
        this.requireValidation = dto.isRequireValidation();
        this.navigate = dto.getNavigate() != null
                ? dto.getNavigate().stream().map(ManagePaymentTransactionStatus::new).collect(Collectors.toList())
                : null;
    }

    public ManagePaymentTransactionStatusDto toAggregate(){
        return new ManagePaymentTransactionStatusDto(
                id, code, name, status, description, requireValidation,
                navigate != null ? navigate.stream().map(ManagePaymentTransactionStatus::toAggregateSimple).collect(Collectors.toList()) : null,
                inProgress, completed, cancelled, applied
        );
    }

    public ManagePaymentTransactionStatusDto toAggregateSimple(){
        return new ManagePaymentTransactionStatusDto(
                id, code, name, status, description, requireValidation, null, inProgress, completed, cancelled, applied
        );
    }
}
