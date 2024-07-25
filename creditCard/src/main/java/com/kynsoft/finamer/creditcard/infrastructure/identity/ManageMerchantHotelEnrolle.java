package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
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
public class ManageMerchantHotelEnrolle implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_merchant_id")
    private ManageMerchant manageMerchant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_hotel_id")
    private ManageHotel manageHotel;

    private String enrolle;

    private String status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    private Boolean deleted = false;
    private LocalDateTime deletedAt;

    public ManageMerchantHotelEnrolle(ManageMerchantHotelEnrolleDto dto) {
        this.id = dto.getId();
        this.manageMerchant = new ManageMerchant(dto.getManageMerchant());
        this.manageHotel = new ManageHotel(dto.getManageHotel());
        this.enrolle = dto.getEnrolle();
        this.status = dto.getStatus();
    }

    public ManageMerchantHotelEnrolleDto toAggregate() {
        return new ManageMerchantHotelEnrolleDto(
                id,
                manageMerchant != null ? manageMerchant.toAggregate() : null,
                manageHotel != null ? manageHotel.toAggregate() : null,
                enrolle, status);
    }

}
