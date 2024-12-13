package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
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
@Table(name = "manage_invoice_transaction_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_invoice_transaction_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageInvoiceTransactionType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(name = "agency_rate_amount")
    private Boolean isAgencyRateAmount;

    @Column(name = "negative")
    private Boolean isNegative;

    @Column(name = "policy_credit")
    private Boolean isPolicyCredit;

    @Column(name = "remark_required")
    private Boolean isRemarkRequired;

    @Column(name = "min_num_of_characters")
    private Integer minNumberOfCharacters;

    @Column(name = "default_remark")
    private String defaultRemark;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean defaults;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean cloneAdjustmentDefault;

    public ManageInvoiceTransactionType(ManageInvoiceTransactionTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.isAgencyRateAmount = dto.getIsAgencyRateAmount();
        this.isNegative = dto.getIsNegative();
        this.isPolicyCredit = dto.getIsPolicyCredit();
        this.isRemarkRequired = dto.getIsRemarkRequired();
        this.minNumberOfCharacters = dto.getMinNumberOfCharacters();
        this.defaultRemark = dto.getDefaultRemark();
        this.defaults = dto.isDefaults();
        this.cloneAdjustmentDefault = dto.isCloneAdjustmentDefault();
    }

    public ManageInvoiceTransactionTypeDto toAggregate(){
        return new ManageInvoiceTransactionTypeDto(
                id, code, description, status, name, isAgencyRateAmount, isNegative, isPolicyCredit,
                isRemarkRequired, minNumberOfCharacters, defaultRemark, defaults, cloneAdjustmentDefault
        );
    }
}
