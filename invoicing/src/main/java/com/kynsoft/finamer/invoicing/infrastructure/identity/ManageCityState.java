package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_city_state")
public class ManageCityState implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_country_id")
    private ManageCountry country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_time_zone_id")
    private ManagerTimeZone timeZone;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageCityState(ManageCityStateDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.country = dto.getCountry() != null ? new ManageCountry(dto.getCountry()) : null;
        this.timeZone = dto.getTimeZone() != null ? new ManagerTimeZone(dto.getTimeZone()) : null;
        this.status = dto.getStatus();
    }

    public ManageCityStateDto toAggregate() {
        return new ManageCityStateDto(
                id, code, name, description, status,
                country != null ? country.toAggregate() : null,
                timeZone != null ? timeZone.toAggregate() : null
        );
    }

    public ManageCityStateDto toAggregateSimple() {
        return new ManageCityStateDto(
                id, code, name, description, status,
                country != null ? country.toAggregate() : null,
                timeZone != null ? timeZone.toAggregate() : null
        );
    }

}
