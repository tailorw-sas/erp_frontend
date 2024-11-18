package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
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
@Table(name = "merchant_language_code")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "merchant_language_code",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class MerchantLanguageCode implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String name;

    private String merchantLanguage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", nullable = false)  // Hace la relación obligatoria
    private ManageLanguage manageLanguage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "merchant_id", nullable = false)  // Hace la relación obligatoria
    private ManageMerchant manageMerchant;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public MerchantLanguageCode(MerchantLanguageCodeDto dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.merchantLanguage = dto.getMerchantLanguage();
        this.manageLanguage = dto.getManageLanguage() != null ? new ManageLanguage(dto.getManageLanguage()) : null;
        this.manageMerchant = dto.getManageMerchant() != null ? new ManageMerchant(dto.getManageMerchant()) : null;
    }

    public MerchantLanguageCodeDto toAggregate(){
        return new MerchantLanguageCodeDto(
                id, name, merchantLanguage,
                manageLanguage != null ? manageLanguage.toAggregate() : null,
                manageMerchant != null ? manageMerchant.toAggregate() : null
        );
    }
}
