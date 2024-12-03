package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
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
@Table(name = "manage_transaction_status")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_transaction_status",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageTransactionStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_transaction_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageTransactionStatus> navigate = new ArrayList<>();

    private Boolean enablePayment;
    private Boolean visible;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean sentStatus;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean refundStatus;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean receivedStatus;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean cancelledStatus;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean declinedStatus;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean reconciledStatus;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean paidStatus;

    public ManageTransactionStatus(ManageTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.enablePayment = dto.getEnablePayment();
        this.visible = dto.getVisible();
        this.navigate = dto.getNavigate() != null ? dto.getNavigate().stream()
                    .map(ManageTransactionStatus::new)
                    .collect(Collectors.toList()) : null;
        this.sentStatus = dto.isSentStatus();
        this.refundStatus = dto.isRefundStatus();
        this.receivedStatus = dto.isReceivedStatus();
        this.cancelledStatus = dto.isCancelledStatus();
        this.declinedStatus = dto.isDeclinedStatus();
        this.reconciledStatus = dto.isReconciledStatus();
        this.paidStatus = dto.isPaidStatus();
    }

    public ManageTransactionStatusDto toAggregate() {
        return new ManageTransactionStatusDto(
                id, code, name, description,
                navigate != null ? navigate.stream().map(ManageTransactionStatus::toAggregateSample).toList() : null,
                enablePayment, visible, status,
                sentStatus, refundStatus, receivedStatus,
                cancelledStatus, declinedStatus, reconciledStatus, paidStatus);
    }

    public ManageTransactionStatusDto toAggregateSample() {
        return new ManageTransactionStatusDto(
                id, code, name, description,
                null, enablePayment, visible, status,
                sentStatus, refundStatus, receivedStatus,
                cancelledStatus, declinedStatus, reconciledStatus, paidStatus);
    }

}