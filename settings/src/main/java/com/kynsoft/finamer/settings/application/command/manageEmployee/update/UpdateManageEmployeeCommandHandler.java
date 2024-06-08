package com.kynsoft.finamer.settings.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmployeeEmailSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeEmailMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeLoginNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageEmployeeCommandHandler implements ICommandHandler<UpdateManageEmployeeCommand> {
    
    private final IManageEmployeeService service;
    private final IManageDepartmentGroupService serviceDepartment;
    private final IManageEmployeeGroupService serviceEmployeeGroup;

    public UpdateManageEmployeeCommandHandler(IManageEmployeeService service,
                                              IManageDepartmentGroupService serviceDepartment,
                                              IManageEmployeeGroupService serviceEmployeeGroup) {
        this.service = service;
        this.serviceDepartment = serviceDepartment;
        this.serviceEmployeeGroup = serviceEmployeeGroup;
    }

    @Override
    public void handle(UpdateManageEmployeeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Merchant Currency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getDepartmentGroup(), "departmentGroup", "Manage Department Group ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployeeGroup(), "employeeGroup", "Manage Employee Group ID cannot be null."));

        ManageEmployeeDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateManageDepartmentGroup(test::setDepartmentGroup, command.getDepartmentGroup(), test.getDepartmentGroup().getId(), update::setUpdate);
        this.updateManageEmployeeGroup(test::setEmployeeGroup, command.getEmployeeGroup(), test.getEmployeeGroup().getId(), update::setUpdate);

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setEmail, command.getEmail(), test.getEmail(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageEmployeeEmailSizeRule(command.getEmail()));
            RulesChecker.checkRule(new ManageEmproyeeEmailMustBeUniqueRule(this.service, command.getEmail(), command.getId()));
        }

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setLoginName, command.getLoginName(), test.getLoginName(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageEmproyeeLoginNameMustBeUniqueRule(this.service, command.getLoginName(), command.getId()));
        }

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setFirstName, command.getFirstName(), test.getFirstName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setLastName, command.getLastName(), test.getLastName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setHash, command.getHash(), test.getHash(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setInnsistCode, command.getInnsistCode(), test.getInnsistCode(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setMiddleName, command.getMiddleName(), test.getMiddleName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setSalt, command.getSalt(), test.getSalt(), update::setUpdate);

        UpdateIfNotNull.updateInteger(test::setIterations, command.getIterations(), test.getIterations(), update::setUpdate);
        UpdateIfNotNull.updateInteger(test::setMemorySize, command.getMemorySize(), test.getMemorySize(), update::setUpdate);
        UpdateIfNotNull.updateInteger(test::setParallelism, command.getParallelism(), test.getParallelism(), update::setUpdate);
        UpdateIfNotNull.updateInteger(test::setPhoneExtension, command.getPhoneExtension(), test.getPhoneExtension(), update::setUpdate);

        UpdateIfNotNull.updateBoolean(test::setIsLock, command.getIsLock(), test.getIsLock(), update::setUpdate);

        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

    }

    private boolean updateManageDepartmentGroup(Consumer<ManageDepartmentGroupDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageDepartmentGroupDto departmentGroupDto = this.serviceDepartment.findById(newValue);
            setter.accept(departmentGroupDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageEmployeeGroup(Consumer<ManageEmployeeGroupDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageEmployeeGroupDto employeeGroupDto = this.serviceEmployeeGroup.findById(newValue);
            setter.accept(employeeGroupDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

}
