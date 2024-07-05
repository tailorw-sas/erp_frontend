package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageVCCTransactionTypeDto;
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
@Table(name = "manage_vcc_transaction_type")
public class ManageVCCTransactionType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;

    private String description;

    private Boolean isActive;

    private Boolean negative;
    private Boolean isDefault;
    private Boolean subcategory;
    private Boolean onlyApplyNet;
    private Boolean policyCredit;
    private Boolean remarkRequired;
    private Integer minNumberOfCharacter;
    private String defaultRemark;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
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
    }

    public ManageVCCTransactionTypeDto toAggregate(){
        return new ManageVCCTransactionTypeDto(id,code, description, status, name, isActive, negative,
                isDefault,
                subcategory,
                onlyApplyNet,
                policyCredit,
                remarkRequired,
                minNumberOfCharacter,
                defaultRemark);
    }

}
