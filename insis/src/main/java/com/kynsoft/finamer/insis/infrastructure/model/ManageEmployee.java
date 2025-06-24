package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_employee")
public class ManageEmployee {

    @Id
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    public LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_employee_agencies_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageAgency> manageAgencyList;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_employee_hotels_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageHotel> manageHotelList;

    public ManageEmployee(ManageEmployeeDto dto){
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.email = dto.getEmail();
        this.updatedAt = dto.getUpdatedAt();
        if(dto.getManageAgencyList() != null){
            this.manageAgencyList = dto.getManageAgencyList().stream()
                    .map(ManageAgency::new)
                    .collect(Collectors.toList());
        }

        if(dto.getManageHotelList() != null){
            this.manageHotelList = dto.getManageHotelList().stream()
                    .map(ManageHotel::new)
                    .collect(Collectors.toList());
        }
    }

    public ManageEmployeeDto toAggregate(){
        return new ManageEmployeeDto(
                id,
                firstName,
                lastName,
                email,
                updatedAt,
                manageHotelList != null ? manageHotelList.stream().map(ManageHotel::toAggregate).collect(Collectors.toList()) : null,
                manageAgencyList != null ? manageAgencyList.stream().map(ManageAgency::toAggregate).collect(Collectors.toList()) : null
        );
    }
}
