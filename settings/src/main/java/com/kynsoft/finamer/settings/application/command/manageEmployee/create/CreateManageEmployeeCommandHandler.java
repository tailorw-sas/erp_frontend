package com.kynsoft.finamer.settings.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmployeeEmailSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeEmailMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeLoginNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageEmployeeCommandHandler implements ICommandHandler<CreateManageEmployeeCommand> {

    private final IManageEmployeeService service;
    private final IManageDepartmentGroupService serviceDepartment;
    private final IManageEmployeeGroupService serviceEmployeeGroup;

    public CreateManageEmployeeCommandHandler(IManageEmployeeService service,
                                              IManageDepartmentGroupService serviceDepartment,
                                              IManageEmployeeGroupService serviceEmployeeGroup) {
        this.service = service;
        this.serviceDepartment = serviceDepartment;
        this.serviceEmployeeGroup = serviceEmployeeGroup;
    }

    @Override
    public void handle(CreateManageEmployeeCommand command) {
        RulesChecker.checkRule(new ManageEmployeeEmailSizeRule(command.getEmail()));
        RulesChecker.checkRule(new ManageEmproyeeLoginNameMustBeUniqueRule(this.service, command.getLoginName(), command.getId()));
        RulesChecker.checkRule(new ManageEmproyeeEmailMustBeUniqueRule(this.service, command.getEmail(), command.getId()));

        ManageEmployeeGroupDto manageEmployeeGroupDto = this.serviceEmployeeGroup.findById(command.getEmployeeGroup());
        ManageDepartmentGroupDto manageDepartmentGroupDto = this.serviceDepartment.findById(command.getDepartmentGroup());

        service.create(new ManageEmployeeDto(
                command.getId(),
                command.getFirstName(),
                command.getLastName(),
                command.getLoginName(),
                command.getEmail(),
                command.getInnsistCode(),
                command.getHash(),
                command.getSalt(),
                command.getParallelism(),
                command.getIterations(),
                command.getMemorySize(),
                command.getIsLock(),
                command.getPhoneExtension(),
                command.getMiddleName(),
                manageDepartmentGroupDto,
                manageEmployeeGroupDto,
                command.getStatus()
        ));
    }
}
