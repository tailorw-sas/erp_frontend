package com.kynsoft.finamer.settings.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageEmployeeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmployeeEmailSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmployeePhoneExtensionRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeEmailMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeLoginNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.*;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.employeePermission.ProducerUpdateEmployeePermissionService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageEmployee.ProducerUpdateManageEmployeeService;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateManageEmployeeCommandHandler implements ICommandHandler<UpdateManageEmployeeCommand> {

    private final IManageEmployeeService service;

    private final IManageDepartmentGroupService serviceDepartment;

    private final IManagePermissionService permissionService;

    private final IManageAgencyService agencyService;

    private final IManageHotelService hotelService;

    private final IManageTradingCompaniesService tradingCompaniesService;

    private final IManageReportService reportService;

    private final ProducerUpdateManageEmployeeService producerUpdateManageEmployeeService;

    public UpdateManageEmployeeCommandHandler(IManageEmployeeService service,
            IManageDepartmentGroupService serviceDepartment,
            IManagePermissionService permissionService,
            IManageAgencyService agencyService,
            IManageHotelService hotelService,
            IManageTradingCompaniesService tradingCompaniesService,
            IManageReportService reportService,
            ProducerUpdateManageEmployeeService producerUpdateManageEmployeeService,
            ProducerUpdateEmployeePermissionService producerUpdateEmployeePermissionService) {
        this.service = service;
        this.serviceDepartment = serviceDepartment;
        this.permissionService = permissionService;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.tradingCompaniesService = tradingCompaniesService;
        this.reportService = reportService;
        this.producerUpdateManageEmployeeService = producerUpdateManageEmployeeService;
    }

    @Override
    public void handle(UpdateManageEmployeeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Merchant Currency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getDepartmentGroup(), "departmentGroup", "Manage Department Group ID cannot be null."));
        RulesChecker.checkRule(new ManageEmployeePhoneExtensionRule(command.getPhoneExtension()));

        ManageEmployeeDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateManageDepartmentGroup(test::setDepartmentGroup, command.getDepartmentGroup(), test.getDepartmentGroup().getId(), update::setUpdate);

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setEmail, command.getEmail(), test.getEmail(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageEmployeeEmailSizeRule(command.getEmail()));
            RulesChecker.checkRule(new ManageEmproyeeEmailMustBeUniqueRule(this.service, command.getEmail(), command.getId()));
        }

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setLoginName, command.getLoginName(), test.getLoginName(), update::setUpdate)) {
            RulesChecker.checkRule(new ManageEmproyeeLoginNameMustBeUniqueRule(this.service, command.getLoginName(), command.getId()));
        }

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setFirstName, command.getFirstName(), test.getFirstName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setLastName, command.getLastName(), test.getLastName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setInnsistCode, command.getInnsistCode(), test.getInnsistCode(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setPhoneExtension, command.getPhoneExtension(), test.getPhoneExtension(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);
        this.updatePermissions(test::setManagePermissionList, command.getManagePermissionList(), test.getManagePermissionList().stream().map(PermissionDto::getId).toList(), update::setUpdate);
        this.updateAgencies(test::setManageAgencyList, command.getManageAgencyList(), test.getManageAgencyList().stream().map(ManageAgencyDto::getId).toList(), update::setUpdate);
        this.updateHotel(test::setManageHotelList, command.getManageHotelList(), test.getManageHotelList().stream().map(ManageHotelDto::getId).toList(), update::setUpdate);
        this.updateTradingCompanies(test::setManageTradingCompaniesList, command.getManageTradingCompaniesList(), test.getManageTradingCompaniesList().stream().map(ManageTradingCompaniesDto::getId).toList(), update::setUpdate);
        this.updateUserType(test::setUserType, command.getUserType(), test.getUserType(), update::setUpdate);
        this.updateReport(test::setManageReportList, command.getManageReportList(), test.getManageReportList().stream().map(ManageReportDto::getId).toList(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producerUpdateManageEmployeeService.update(new UpdateManageEmployeeKafka(test.getId(),
                    test.getFirstName(), test.getLastName(), test.getEmail()));
        }

    }

    private void updateManageDepartmentGroup(Consumer<ManageDepartmentGroupDto> setter, UUID newValue, UUID oldValue,
                                             Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageDepartmentGroupDto departmentGroupDto = this.serviceDepartment.findById(newValue);
            setter.accept(departmentGroupDto);
            update.accept(1);

        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private void updatePermissions(Consumer<List<PermissionDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<PermissionDto> dtoList = permissionService.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

        }
    }

    private void updateAgencies(Consumer<List<ManageAgencyDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManageAgencyDto> dtoList = agencyService.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

        }
    }

    private void updateHotel(Consumer<List<ManageHotelDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManageHotelDto> dtoList = hotelService.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

        }
    }

    private boolean updateTradingCompanies(Consumer<List<ManageTradingCompaniesDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManageTradingCompaniesDto> dtoList = tradingCompaniesService.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateUserType(Consumer<UserType> setter, UserType newValue, UserType oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateReport(Consumer<List<ManageReportDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManageReportDto> dtoList = reportService.findByIds(newValue);
            setter.accept(dtoList);
            update.accept(1);

            return true;
        }
        return false;
    }
}
