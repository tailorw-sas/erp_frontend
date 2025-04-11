package com.kynsoft.finamer.settings.infrastructure.identity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_city_state")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_city_state",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
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
                timeZone != null ? timeZone.toAggregate() : null);
    }

    public ManageCityState(UUID id,
                           String code,
                           String name,
                           String description,
                           ManageCountry manageCountry,
                           ManagerTimeZone managerTimeZone,
                           Status status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.country = Objects.nonNull(manageCountry) ? manageCountry : null;
        this.timeZone = Objects.nonNull(managerTimeZone) ? managerTimeZone : null;
    }

}
