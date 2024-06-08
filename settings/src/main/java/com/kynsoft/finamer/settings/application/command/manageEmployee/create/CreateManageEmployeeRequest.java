package com.kynsoft.finamer.settings.application.command.manageEmployee.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateManageEmployeeRequest {
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
    private UUID departmentGroup;
    private UUID employeeGroup;
    private Status status;
}
