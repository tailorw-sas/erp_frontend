package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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
@Table(name = "manage_merchant")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_merchant",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageMerchant implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "b2bpartner_id")
    private ManageB2BPartner b2bPartner;

    @Column(nullable = true)
    private Boolean defaultm;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @OneToOne(mappedBy = "manageMerchant") // Relación inversa
    private ManagerMerchantConfig managerMerchantConfig;

    public ManageMerchant(ManageMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.b2bPartner = new ManageB2BPartner(dto.getB2bPartner());
        this.defaultm = dto.getDefaultm();
        this.status = (dto.getStatus());
    }

    public ManageMerchantDto toAggregate() {
        return new ManageMerchantDto(id, code, description,
                b2bPartner != null ? b2bPartner.toAggregate() : null, defaultm, status);
    }

}

