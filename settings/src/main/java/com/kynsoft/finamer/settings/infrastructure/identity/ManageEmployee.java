package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_employee")
public class ManageEmployee implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_group_id")
    private ManageDepartmentGroup departmentGroup;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    private String firstName;
    private String lastName;
    private String loginName;
    private String email;
    private String innsistCode;
    private String phoneExtension;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<ManagePermission> managePermissionList;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_employee_tradingcompanies_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageTradingCompanies> manageTradingCompaniesList;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_employee_report_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageReport> manageReportList;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    public ManageEmployee(ManageEmployeeDto dto) {
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.loginName = dto.getLoginName();
        this.email = dto.getEmail();
        this.innsistCode = dto.getInnsistCode();
        this.phoneExtension = dto.getPhoneExtension();
        this.departmentGroup = dto.getDepartmentGroup() != null ? new ManageDepartmentGroup(dto.getDepartmentGroup()) : null;
        this.status = dto.getStatus();
        if(dto.getManagePermissionList() != null){
            this.managePermissionList = dto.getManagePermissionList().stream()
                    .map(ManagePermission::new)
                    .collect(Collectors.toList());
        }

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

        if(dto.getManageTradingCompaniesList() != null){
            this.manageTradingCompaniesList = dto.getManageTradingCompaniesList().stream()
                    .map(ManageTradingCompanies::new)
                    .collect(Collectors.toList());
        }
        this.userType = dto.getUserType();

        if(dto.getManageReportList() != null){
            this.manageReportList = dto.getManageReportList().stream()
                    .map(ManageReport::new)
                    .collect(Collectors.toList());
        }
    }

    public ManageEmployeeDto toAggregate() {
        return new ManageEmployeeDto(
                id, 
                firstName, 
                lastName, 
                loginName, 
                email, 
                innsistCode,
                phoneExtension,
                departmentGroup != null ? departmentGroup.toAggregate() : null,
                status,
                managePermissionList != null ? managePermissionList.stream().map(ManagePermission::toAggregate).collect(Collectors.toList()) : null,
                manageAgencyList != null ? manageAgencyList.stream().map(ManageAgency::toAggregate).collect(Collectors.toList()) : null,
                manageHotelList != null ? manageHotelList.stream().map(ManageHotel::toAggregate).collect(Collectors.toList()) : null,
                manageTradingCompaniesList != null ? manageTradingCompaniesList.stream().map(ManageTradingCompanies::toAggregate).collect(Collectors.toList()) :  null,
                userType,
                manageReportList != null ? manageReportList.stream().map(ManageReport::toAggregate).collect(Collectors.toList()) : null
        );
    }

}