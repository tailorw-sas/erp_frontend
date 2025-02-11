package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ManageEmployeeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_employee")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "manage_employee",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ManageEmployee implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

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

    public ManageEmployee(ManageEmployeeDto dto) {
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.email = dto.getEmail();

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

    public ManageEmployeeDto toAggregate() {
        return new ManageEmployeeDto(
                id, 
                firstName, 
                lastName, 
                email,
                manageAgencyList != null ? manageAgencyList.stream().map(ManageAgency::toAggregate).collect(Collectors.toList()) : null,
                manageHotelList != null ? manageHotelList.stream().map(ManageHotel::toAggregate).collect(Collectors.toList()) : null
        );
    }

}