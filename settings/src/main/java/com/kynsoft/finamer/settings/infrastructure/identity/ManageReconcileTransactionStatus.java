package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateReconcileTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_reconcile_transaction_status")
public class ManageReconcileTransactionStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;

    private String description;

    private Boolean requireValidation;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_reconcile_transaction_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageReconcileTransactionStatus> relatedStatuses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageReconcileTransactionStatus(ManageReconcileTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.requireValidation = dto.getRequireValidation();
        if (dto.getRelatedStatuses() != null) {
            this.relatedStatuses = dto.getRelatedStatuses().stream()
                    .map(ManageReconcileTransactionStatus::new)
                    .collect(Collectors.toList());
        }
    }
    public ManageReconcileTransactionStatusDto toAggregateSample(){
        return new ManageReconcileTransactionStatusDto(
                id,code, description, status, name, requireValidation,
                null
        );
    }

    public ManageReconcileTransactionStatusDto toAggregate(){
        return new ManageReconcileTransactionStatusDto(id,code, description, status, name, requireValidation, relatedStatuses != null ? relatedStatuses.stream().map(ManageReconcileTransactionStatus::toAggregateSample).toList() : null);
    }

}
