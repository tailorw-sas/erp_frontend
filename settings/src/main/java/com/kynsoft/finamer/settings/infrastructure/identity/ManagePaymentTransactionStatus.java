package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigatePaymentTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_transaction_status")
public class ManagePaymentTransactionStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;

    private String description;

    private Boolean requireValidation;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_payment_transaction_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManagePaymentTransactionStatus> relatedStatuses = new ArrayList<>();
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManagePaymentTransactionStatus(ManagePaymentTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        if (dto.getRelatedStatuses() != null) {
            this.relatedStatuses = dto.getRelatedStatuses().stream()
                    .map(ManagePaymentTransactionStatus::new)
                    .collect(Collectors.toList());
        }
        this.requireValidation = dto.getRequireValidation();
    }

    public ManagePaymentTransactionStatusDto toAggregateSample(){
        return new ManagePaymentTransactionStatusDto(
                id,code, description, status, name, requireValidation,
                null
        );
    }

    public ManagePaymentTransactionStatusDto toAggregate(){
        return new ManagePaymentTransactionStatusDto(id,code, description, status, name, requireValidation,  relatedStatuses != null ? relatedStatuses.stream().map(ManagePaymentTransactionStatus::toAggregateSample).toList() : null);
    }

}
