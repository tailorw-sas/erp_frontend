package com.kynsoft.finamer.settings.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManageEmployeeCommand implements ICommand {

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
    private UUID departmentGroup;
    private UUID employeeGroup;
    private Status status;

    public UpdateManageEmployeeCommand(UUID id, String firstName, String lastName, String loginName, String email, 
                                       String innsistCode, String hash, String salt, int parallelism, 
                                       int iterations, int memorySize, Boolean isLock, int phoneExtension, 
                                       String middleName, UUID departmentGroup, UUID employeeGroup, Status status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.email = email;
        this.innsistCode = innsistCode;
        this.hash = hash;
        this.salt = salt;
        this.parallelism = parallelism;
        this.iterations = iterations;
        this.memorySize = memorySize;
        this.isLock = isLock;
        this.phoneExtension = phoneExtension;
        this.middleName = middleName;
        this.departmentGroup = departmentGroup;
        this.employeeGroup = employeeGroup;
        this.status = status;
    }

    public static UpdateManageEmployeeCommand fromRequest(UpdateManageEmployeeRequest request, UUID id) {
        return new UpdateManageEmployeeCommand(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getLoginName(),
                request.getEmail(),
                request.getInnsistCode(),
                request.getHash(),
                request.getSalt(),
                request.getParallelism(),
                request.getIterations(),
                request.getMemorySize(),
                request.getIsLock(),
                request.getPhoneExtension(),
                request.getMiddleName(),
                request.getDepartmentGroup(),
                request.getEmployeeGroup(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageEmployeeMessage(id);
    }
}
