package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeResponse implements IResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String loginName;
    private String email;
    private String innsistCode;
    private String hash;
    private String salt;
    private int parallelism;
    private int iterations;
    private int memorySize;
    private Boolean isLock;
    private int phoneExtension;
    private String middleName;

    private ManageDepartmentGroupResponse departmentGroup;
    private ManageEmployeeGroupResponse employeeGroup;
    private Status status;

    public ManageEmployeeResponse(ManageEmployeeDto dto) {
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
        this.departmentGroup = dto.getDepartmentGroup() != null ? new ManageDepartmentGroupResponse(dto.getDepartmentGroup()) : null;
        this.employeeGroup = dto.getEmployeeGroup() != null ? new ManageEmployeeGroupResponse(dto.getEmployeeGroup()) : null;
        this.status = dto.getStatus();
    }

}