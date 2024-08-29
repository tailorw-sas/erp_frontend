package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

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
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Column(columnDefinition = "serial", name = "autogen_code")
    @Generated(event = EventType.INSERT)
    private Long autogen_code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trading_company_id")
    private ManageTradingCompanies manageTradingCompanies;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    private Boolean isVirtual;

    private String status;
    @Column(columnDefinition = "boolean default false")
    private boolean requiresFlatRate;

    private Boolean autoApplyCredit;

    public ManageHotel(ManageHotelDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();

        this.name = dto.getName();
        this.manageTradingCompanies = dto.getManageTradingCompanies() != null
                ? new ManageTradingCompanies(dto.getManageTradingCompanies())
                : null;
        this.isVirtual=dto.isVirtual();
        this.status = dto.getStatus();
        this.requiresFlatRate=dto.isRequiresFlatRate();
        this.autoApplyCredit = dto.getAutoApplyCredit();
    }

    public ManageHotelDto toAggregate() {
        return new ManageHotelDto(
                id, code, name, manageTradingCompanies != null ? manageTradingCompanies.toAggregate() : null, null,
                isVirtual != null ? isVirtual : false, status,requiresFlatRate, autoApplyCredit);
    }
}
