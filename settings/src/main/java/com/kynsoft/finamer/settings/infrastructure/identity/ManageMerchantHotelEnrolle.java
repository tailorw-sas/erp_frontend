package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
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
@Table(name = "manage_merchant_hotel_enrolle")
public class ManageMerchantHotelEnrolle implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_merchant_id")
    private ManagerMerchant managerMerchant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_currency_id")
    private ManagerCurrency managerCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_hotel_id")
    private ManageHotel managerHotel;

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
        this.managerMerchant = new ManagerMerchant(dto.getManagerMerchant());
        this.managerCurrency = new ManagerCurrency(dto.getManagerCurrency());
        this.managerHotel = new ManageHotel(dto.getManagerHotel());
        this.enrrolle = dto.getEnrrolle();
        this.key = dto.getKey();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
    }

    public ManageMerchantHotelEnrolleDto toAggregate() {
        return new ManageMerchantHotelEnrolleDto(
                id,
                managerMerchant != null ? managerMerchant.toAggregate() : null,
                managerCurrency != null ? managerCurrency.toAggregate() : null,
                managerHotel != null ? managerHotel.toAggregate() : null,
                enrrolle, key, description, status);
    }

}
