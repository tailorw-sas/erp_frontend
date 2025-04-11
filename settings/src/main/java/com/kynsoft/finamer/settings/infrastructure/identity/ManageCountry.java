package com.kynsoft.finamer.settings.infrastructure.identity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.kafka.common.metrics.Stat;
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
@Table(name = "manage_country")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_country",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageCountry implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_language_id")
    private ManagerLanguage managerLanguage;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;


    @OneToMany(mappedBy = "country", fetch = FetchType.EAGER)
    private List<ManageAgency> agencies;

    public ManageCountry(ManagerCountryDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.dialCode = dto.getDialCode();
        this.iso3 = dto.getIso3();
        this.isDefault = dto.getIsDefault();
        this.managerLanguage = dto.getManagerLanguage() != null ? new ManagerLanguage(dto.getManagerLanguage()) : null;
    }

    public ManagerCountryDto toAggregate() {
        return new ManagerCountryDto(
                id, 
                code, 
                name, 
                description, 
                dialCode, 
                iso3, 
                isDefault, 
               managerLanguage != null ?  managerLanguage.toAggregate() : null,
                status
        );
    }

    public ManageCountry(UUID id,
                         String code,
                         String name,
                         String description,
                         String dialCode,
                         String iso3,
                         Boolean isDefault,
                         ManagerLanguage managerLanguage,
                         Status status){
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.dialCode = dialCode;
        this.iso3 = iso3;
        this.isDefault = isDefault;
        this.managerLanguage = Objects.nonNull(managerLanguage) ? managerLanguage : null;
        this.status = status;
    }

}