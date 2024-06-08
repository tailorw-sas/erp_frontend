package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
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
@Table(name = "manager_country")
public class ManagerCountry implements Serializable {

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
    private ManagerLanguage managerLanguage;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;

    public ManagerCountry(ManagerCountryDto dto) {
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
                managerLanguage.toAggregate(), 
                status
        );
    }

}