package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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
@Table(name = "manager_merchant")
public class ManagerMerchant implements Serializable {

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
    @OneToOne(mappedBy = "managerMerchant", cascade = CascadeType.ALL) // Relación inversa
    private ManagerMerchantConfig managerMerchantConfig;

    public ManagerMerchant(ManagerMerchantDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.b2bPartner = new ManageB2BPartner(dto.getB2bPartner());
        this.defaultm = dto.getDefaultm();
        this.status = dto.getStatus();
    }

    public ManagerMerchantDto toAggregate() {
        return new ManagerMerchantDto(id, code, description,
                b2bPartner != null ? b2bPartner.toAggregate() : null, defaultm, status);
    }

}
