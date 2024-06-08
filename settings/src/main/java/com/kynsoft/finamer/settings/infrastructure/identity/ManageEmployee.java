package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
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
@Table(name = "manage_employee")
public class ManageEmployee implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String firstName;
    private String lastName;
    @Column(nullable = true, unique = true)
    private String loginName;
    @Column(nullable = true, unique = true)
    private String email;
    private String innsistCode;
    private String hash;
    private String salt;
    private Integer parallelism;
    private Integer iterations;
    private Integer memorySize;
    private Boolean isLock;
    private Integer phoneExtension;
    private String middleName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_group_id")
    private ManageDepartmentGroup departmentGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_group_id")
    private ManageEmployeeGroup employeeGroup;

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

    public ManageEmployee(ManageEmployeeDto dto) {
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.loginName = dto.getLoginName();
        this.email = dto.getEmail();
        this.innsistCode = dto.getInnsistCode();
        this.hash = dto.getHash();
        this.salt = dto.getSalt();
        this.parallelism = dto.getParallelism();
        this.iterations = dto.getIterations();
        this.memorySize = dto.getMemorySize();
        this.isLock = dto.getIsLock();
        this.phoneExtension = dto.getPhoneExtension();
        this.middleName = dto.getMiddleName();
        this.departmentGroup = dto.getDepartmentGroup() != null ? new ManageDepartmentGroup(dto.getDepartmentGroup()) : null;
        this.employeeGroup = dto.getEmployeeGroup() != null ? new ManageEmployeeGroup(dto.getEmployeeGroup()) : null;
        this.status = dto.getStatus();
    }

    public ManageEmployeeDto toAggregate() {
        return new ManageEmployeeDto(
                id, 
                firstName, 
                lastName, 
                loginName, 
                email, 
                innsistCode, 
                hash, 
                salt, 
                parallelism, 
                iterations, 
                memorySize, 
                isLock, 
                phoneExtension, 
                middleName, 
                departmentGroup.toAggregate(), 
                employeeGroup.toAggregate(), 
                status
        );
    }

}