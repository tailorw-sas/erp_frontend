package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.*;
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
@Table(name = "manage_hotel")
public class ManageHotel implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    private String babelCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private ManageCountry manageCountry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_state_id")
    private ManageCityState manageCityState;

    private String city;

    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currency_id")
    private ManagerCurrency manageCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id")
    private ManageRegion manageRegion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trading_company_id")
    private ManageTradingCompanies manageTradingCompanies;

    private Boolean applyByTradingCompany;

    private String prefixToInvoice;

    private Boolean isVirtual;

    private Boolean requiresFlatRate;

    private Boolean isApplyByVCC;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean autoApplyCredit;

    public ManageHotel(ManageHotelDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.babelCode = dto.getBabelCode();
        this.manageCountry = new ManageCountry(dto.getManageCountry());
        this.manageCityState = new ManageCityState(dto.getManageCityState());
        this.city = dto.getCity();
        this.address = dto.getAddress();
        this.manageCurrency = new ManagerCurrency(dto.getManageCurrency());
        this.manageRegion = new ManageRegion(dto.getManageRegion());
        this.manageTradingCompanies = dto.getManageTradingCompanies() != null ? new ManageTradingCompanies(dto.getManageTradingCompanies()) : null;
        this.applyByTradingCompany = dto.getApplyByTradingCompany();
        this.prefixToInvoice = dto.getPrefixToInvoice();
        this.isVirtual = dto.getIsVirtual();
        this.requiresFlatRate = dto.getRequiresFlatRate();
        this.isApplyByVCC = dto.getIsApplyByVCC();
        this.autoApplyCredit = dto.getAutoApplyCredit();
    }

    public ManageHotelDto toAggregate() {
        return new ManageHotelDto(
                id,
                code,
                description,
                status,
                name,
                babelCode,
                manageCountry != null ? manageCountry.toAggregate() : null,
                manageCityState != null ? manageCityState.toAggregate() : null,
                city,
                address,
                manageCurrency != null ? manageCurrency.toAggregate() : null,
                manageRegion != null ? manageRegion.toAggregate() : null,
                manageTradingCompanies != null ? manageTradingCompanies.toAggregate() : null,
                applyByTradingCompany, prefixToInvoice, isVirtual, requiresFlatRate, isApplyByVCC, autoApplyCredit
        );
    }
}
