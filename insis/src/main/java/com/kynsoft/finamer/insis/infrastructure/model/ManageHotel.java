package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_hotel")
public class ManageHotel implements Serializable {
    @Id
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private String status;

    @Column(name = "deleted")
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trading_company_id")
    private ManageTradingCompany manageTradingCompany;


    public ManageHotel(ManageHotelDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.deleted = dto.isDeleted();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.manageTradingCompany = new ManageTradingCompany(dto.getManageTradingCompany());
    }

    public ManageHotel(UUID id,
                       String code,
                       String name,
                       String status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public ManageHotelDto toAggregate(){
        return new ManageHotelDto(
                id,
                code,
                name,
                status,
                deleted,
                updatedAt,
                manageTradingCompany != null ? manageTradingCompany.toAggregate() : null
        );
    }
}
