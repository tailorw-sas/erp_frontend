package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
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
    private ManagerCountry country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_time_zone_id")
    private ManagerTimeZone timeZone;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = true)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;

    public ManageCityState(ManageCityStateDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.country = dto.getCountry() != null ? new ManagerCountry(dto.getCountry()) : null;
        this.timeZone = dto.getTimeZone() != null ? new ManagerTimeZone(dto.getTimeZone()) : null;
        this.status = dto.getStatus();
    }

    public ManageCityStateDto toAggregate() {
        return new ManageCityStateDto(id, code, name, description, status, country.toAggregate(), timeZone.toAggregate());
    }

}
