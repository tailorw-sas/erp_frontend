package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.io.Serializable;
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

    private Boolean isApplyInvoice;
    private String company;
    private String cif;
    private String address;
    private String status;

    @Column(columnDefinition = "serial", name = "autogen_code")
    @Generated(event = EventType.INSERT)
    private Long autogen_code;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private ManageCountry country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_state_id")
    private ManageCityState cityState;

    private String city;

    private String zipCode;

    private String innsistCode;

    public ManageTradingCompanies(ManageTradingCompaniesDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.isApplyInvoice = dto.getIsApplyInvoice();
        this.cif = dto.getCif();
        this.address = dto.getAddress();
        this.company = dto.getCompany();
        this.status = dto.getStatus();
        this.city = dto.getCity();
        this.zipCode = dto.getZipCode();
        this.innsistCode = dto.getInnsistCode();
        this.country = dto.getCountry() != null ? new ManageCountry(dto.getCountry()) : null;
        this.cityState = dto.getCityState() != null ? new ManageCityState(dto.getCityState()) : null;
        this.zipCode = dto.getZipCode();
    }

    public ManageTradingCompaniesDto toAggregate() {
        return new ManageTradingCompaniesDto(
                id, code, isApplyInvoice, autogen_code, this.cif, this.address, this.company, status,
                description,
                country != null ? country.toAggregate() : null,
                cityState != null ? cityState.toAggregate() : null,
                city, zipCode, innsistCode);
    }
}
