package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import jakarta.persistence.*;
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

    private String babelCode;
    private String city;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private ManageCountry manageCountry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_state_id")
    private ManageCityState manageCityState;

    private String address;

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private InvoiceCloseOperation closeOperation;  // Relación uno a uno con InvoiceCloseOperation

    public ManageHotel(ManageHotelDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();

        this.name = dto.getName();
        this.manageTradingCompanies = dto.getManageTradingCompanies() != null
                ? new ManageTradingCompanies(dto.getManageTradingCompanies())
                : null;
        this.isVirtual = dto.isVirtual();
        this.status = dto.getStatus();
        this.requiresFlatRate = dto.isRequiresFlatRate();
        this.autoApplyCredit = dto.getAutoApplyCredit();
        this.babelCode = dto.getBabelCode();
        this.address = dto.getAddress();
        this.manageCityState = dto.getManageCityState() != null ? new ManageCityState(dto.getManageCityState()) : null;
        this.manageCountry = dto.getManageCountry() != null ? new ManageCountry(dto.getManageCountry()) : null;
        this.city = dto.getCity();
    }

    public ManageHotelDto toAggregate() {
        return new ManageHotelDto(
                id, code, name, manageTradingCompanies != null ? manageTradingCompanies.toAggregate() : null, null,
                isVirtual != null ? isVirtual : false, status, requiresFlatRate, autoApplyCredit,
                address,
                babelCode,
                manageCountry != null ? manageCountry.toAggregate() : null,
                manageCityState != null ? manageCityState.toAggregateSimple() : null,
                city
        );
    }
}
