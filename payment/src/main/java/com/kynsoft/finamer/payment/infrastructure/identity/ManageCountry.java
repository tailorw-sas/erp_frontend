package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageCountryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_country")
public class ManageCountry {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    private String name;
    private String description;
    private Boolean isDefault;
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_language_id")
    private ManageLanguage managerLanguage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<ManageAgency> agencies;

    private String iso3;

    public ManageCountry(ManageCountryDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.isDefault = dto.getIsDefault();
        this.managerLanguage = dto.getManagerLanguage() != null ? new ManageLanguage(dto.getManagerLanguage()) : null;
        this.iso3 = dto.getIso3();
    }

    public ManageCountryDto toAggregate() {
        return new ManageCountryDto(
                id,
                code,
                name,
                description,
                isDefault,
                status,
                Objects.nonNull(managerLanguage) ? managerLanguage.toAggregate() : null,
                iso3
        );
    }
}
