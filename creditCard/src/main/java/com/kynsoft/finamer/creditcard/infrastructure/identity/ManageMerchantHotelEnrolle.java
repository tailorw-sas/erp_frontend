package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
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
@Table(name = "manage_merchant_hotel_enrolle")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_merchant_hotel_enrolle",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageMerchantHotelEnrolle implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_merchant_id")
    private ManageMerchant manageMerchant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_currency_id")
    private ManagerCurrency managerCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_hotel_id")
    private ManageHotel manageHotel;

    private String enrrolle;
    private String key;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageMerchantHotelEnrolle(ManageMerchantHotelEnrolleDto dto) {
        this.id = dto.getId();
        this.manageMerchant = new ManageMerchant(dto.getManagerMerchant());
        this.managerCurrency = new ManagerCurrency(dto.getManagerCurrency());
        this.manageHotel = new ManageHotel(dto.getManagerHotel());
        this.enrrolle = dto.getEnrrolle();
        this.key = dto.getKey();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

    public ManageMerchantHotelEnrolleDto toAggregate() {
        return new ManageMerchantHotelEnrolleDto(
                id,
                manageMerchant != null ? manageMerchant.toAggregate() : null,
                managerCurrency != null ? managerCurrency.toAggregate() : null,
                manageHotel != null ? manageHotel.toAggregate() : null,
                enrrolle, key, description, status);
    }

}
