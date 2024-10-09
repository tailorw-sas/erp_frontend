package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageAgencyContactDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_agency_contact")
public class ManageAgencyContact implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "manage_agency_id")
    private ManageAgency manageAgency;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "manage_region_id")
    private ManageRegion manageRegion;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_contact_hotels",
            joinColumns = @JoinColumn(name = "contact_id"),
            inverseJoinColumns = @JoinColumn(name = "hotel_id")
    )
    private List<ManageHotel> manageHotel;

    private String emailContact;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ManageAgencyContact(ManageAgencyContactDto dto) {
        this.id = dto.getId();
        this.manageAgency = dto.getManageAgency() != null ? new ManageAgency(dto.getManageAgency()) : null;
        this.manageRegion = dto.getManageRegion() != null ? new ManageRegion(dto.getManageRegion()) : null;
        this.manageHotel = dto.getManageHotel() != null
                ? dto.getManageHotel().stream().map(ManageHotel::new).collect(Collectors.toList())
                : null;
        this.emailContact = dto.getEmailContact();
    }

    public ManageAgencyContactDto toAggregate(){
        return new ManageAgencyContactDto(
                id,
                manageAgency != null ? manageAgency.toAggregateSample() : null,
                manageRegion != null ? manageRegion.toAggregate() : null,
                manageHotel != null ? manageHotel.stream().map(ManageHotel::toAggregate).collect(Collectors.toList()) : null,
                emailContact
        );
    }
}
