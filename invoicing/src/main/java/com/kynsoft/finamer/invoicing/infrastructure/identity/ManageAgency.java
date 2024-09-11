package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_agency")
public class ManageAgency {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    @Column(nullable = true)
    private Boolean deleted = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_client_id")
    private ManageClient client;

    private String name;
    private String cif;
    private String address;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    private EGenerationType generationType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_b2bpartner_id")
    private ManageB2BPartner sentB2BPartner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_city_state_id")
    private ManageCityState cityState;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_country_id")
    private ManageCountry country;

    private String status;

    public ManageAgency(ManageAgencyDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.client = dto.getClient() != null ? new ManageClient(dto.getClient()) : null;
        this.generationType=dto.getGenerationType();
        this.status=dto.getStatus();
        this.cif= dto.getCif();
        this.address= dto.getAddress();
        this.sentB2BPartner= new ManageB2BPartner(dto.getSentB2BPartner());
        this.cityState= new ManageCityState(dto.getCityState());
        this.country= new ManageCountry(dto.getCountry());
    }

    public ManageAgencyDto toAggregate() {
        return new ManageAgencyDto(
                id, code, name, client != null ? client.toAggregate() : null,generationType,status,cif,address,
                Objects.nonNull(sentB2BPartner)?sentB2BPartner.toAggregate():null,
                Objects.nonNull(cityState)?cityState.toAggregate():null,
                Objects.nonNull(country)?country.toAggregate():null);
    }

    public ManageAgencyDto toAggregateSample() {
        return new ManageAgencyDto(
                id, code, name, client != null ? client.toAggregate() : null,generationType,status,cif,address,
                Objects.nonNull(sentB2BPartner)?sentB2BPartner.toAggregate():null,
                Objects.nonNull(cityState)?cityState.toAggregate():null,
                Objects.nonNull(country)?country.toAggregate():null);
    }
}
