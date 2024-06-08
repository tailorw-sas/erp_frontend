package com.kynsof.identity.infrastructure.identity;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Business {
    @Id
    @Column(name = "id")
    protected UUID id;
    private String name;
    private String latitude;
    private String longitude;
    private String description;
    private String logo;
    private String ruc;
    @Column()
    private String address;
    @Enumerated(EnumType.STRING)
    private EBusinessStatus status;
    private boolean deleted;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserPermissionBusiness> userRoleBusinesses = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "geographicLocation_id")
    private GeographicLocation geographicLocation;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Business(BusinessDto business) {
        this.id = business.getId();
        this.name = business.getName();
        this.latitude = business.getLatitude();
        this.longitude = business.getLongitude();
        this.description = business.getDescription();
        this.logo = business.getLogo();
        this.ruc = business.getRuc();
        this.status = business.getStatus();
        this.deleted = business.isDeleted();
        this.geographicLocation = business.getGeographicLocationDto() != null ? new GeographicLocation(business.getGeographicLocationDto()) : null;
        this.address = business.getAddress();
    }

    public BusinessDto toAggregate () {
        BusinessDto businessDto = new BusinessDto(
                id, 
                name, 
                latitude, 
                longitude, 
                description, 
                logo,
                ruc, 
                status, 
                geographicLocation != null ? geographicLocation.toAggregate() : null,
                address
        );
        businessDto.setCreateAt(createdAt);
        return businessDto;
    }
}
