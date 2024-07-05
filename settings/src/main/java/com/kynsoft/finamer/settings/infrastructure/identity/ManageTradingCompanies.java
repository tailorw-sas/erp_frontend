package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
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
@Table(name = "manage_trading_companies")
public class ManageTradingCompanies implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String company;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    private String cif;

    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private ManageCountry country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_state_id")
    private ManageCityState cityState;

    private String city;

    private Long zipCode;

    private String innsistCode;

    private Boolean isApplyInvoice;

    public ManageTradingCompanies(ManageTradingCompaniesDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.company = dto.getCompany();
        this.cif = dto.getCif();
        this.address = dto.getAddress();
        this.country = new ManageCountry(dto.getCountry());
        this.cityState = new ManageCityState(dto.getCityState());
        this.city = dto.getCity();
        this.zipCode = dto.getZipCode();
        this.innsistCode = dto.getInnsistCode();
        this.isApplyInvoice = dto.getIsApplyInvoice();
    }

    public ManageTradingCompaniesDto toAggregate(){
        return new ManageTradingCompaniesDto(
                id,code, description, status, company, cif, address,
                country != null ? country.toAggregate() : null,
                cityState != null ? cityState.toAggregate() : null,
                city, zipCode, innsistCode, isApplyInvoice
        );
    }
}
