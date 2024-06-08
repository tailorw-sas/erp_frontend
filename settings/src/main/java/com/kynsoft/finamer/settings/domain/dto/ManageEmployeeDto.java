package com.kynsoft.finamer.settings.domain.dto;

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
public class ManageEmployeeDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String loginName;
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

    private ManageDepartmentGroupDto departmentGroup;
    private ManageEmployeeGroupDto employeeGroup;
    private Status status;

}