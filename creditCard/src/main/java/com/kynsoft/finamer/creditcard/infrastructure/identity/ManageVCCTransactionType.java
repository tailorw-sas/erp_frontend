package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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
@Table(name = "manage_vcc_transaction_type")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_vcc_transaction_type",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageVCCTransactionType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    private String name;

    private String description;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean isActive;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean negative;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean isDefault;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean subcategory;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean onlyApplyNet;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean policyCredit;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean remarkRequired;

    private Integer minNumberOfCharacter;

    private String defaultRemark;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private boolean manual;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updateAt;

    public ManageVCCTransactionType(ManageVCCTransactionTypeDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.isActive = dto.getIsActive();
        this.negative = dto.getNegative();
        this.isDefault = dto.getIsDefault();
        this.onlyApplyNet = dto.getOnlyApplyNet();
        this.policyCredit = dto.getPolicyCredit();
        this.remarkRequired = dto.getRemarkRequired();
        this.subcategory = dto.getSubcategory();
        this.minNumberOfCharacter = dto.getMinNumberOfCharacter();
        this.defaultRemark = dto.getDefaultRemark();
        this.manual = dto.isManual();
    }

    public ManageVCCTransactionTypeDto toAggregate(){
        return new ManageVCCTransactionTypeDto(id,code, description, status, name, isActive, negative,
                isDefault,
                subcategory,
                onlyApplyNet,
                policyCredit,
                remarkRequired,
                minNumberOfCharacter,
                defaultRemark, manual);
    }

}
