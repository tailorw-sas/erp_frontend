package com.kynsoft.finamer.settings.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageEmployeeKafka;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmployeeEmailSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmployeePhoneExtensionRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeEmailMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageEmployee.ManageEmproyeeLoginNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.*;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageEmployee.ProducerReplicateManageEmployeeService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManageEmployeeCommandHandler implements ICommandHandler<CreateManageEmployeeCommand> {

    private final IManageEmployeeService service;
    private final IManageDepartmentGroupService serviceDepartment;

    private final IManagePermissionService permissionService;

    private final IManageAgencyService agencyService;

    private final IManageHotelService hotelService;

    private final IManageTradingCompaniesService tradingCompaniesService;

    private final IManageReportService reportService;

    private final ProducerReplicateManageEmployeeService producerReplicateManageEmployeeService;

    public CreateManageEmployeeCommandHandler(IManageEmployeeService service,
                                              IManageDepartmentGroupService serviceDepartment, 
                                              IManagePermissionService permissionService, 
                                              IManageAgencyService agencyService, 
                                              IManageHotelService hotelService, 
                                              IManageTradingCompaniesService tradingCompaniesService, 
                                              IManageReportService reportService,
                                              ProducerReplicateManageEmployeeService producerReplicateManageEmployeeService) {
        this.service = service;
        this.serviceDepartment = serviceDepartment;
        this.permissionService = permissionService;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.tradingCompaniesService = tradingCompaniesService;
        this.reportService = reportService;
        this.producerReplicateManageEmployeeService = producerReplicateManageEmployeeService;
    }

    @Override
    public void handle(CreateManageEmployeeCommand command) {
        RulesChecker.checkRule(new ManageEmployeeEmailSizeRule(command.getEmail()));
        RulesChecker.checkRule(new ManageEmproyeeLoginNameMustBeUniqueRule(this.service, command.getLoginName(), command.getId()));
        RulesChecker.checkRule(new ManageEmproyeeEmailMustBeUniqueRule(this.service, command.getEmail(), command.getId()));
        RulesChecker.checkRule(new ManageEmployeePhoneExtensionRule(command.getPhoneExtension()));

        ManageDepartmentGroupDto manageDepartmentGroupDto = this.serviceDepartment.findById(command.getDepartmentGroup());

        List<ManagePermissionDto> permissionList = permissionService.findByIds(command.getManagePermissionList());
        List<ManageAgencyDto> agencyList = agencyService.findByIds(command.getManageAgencyList());
        List<ManageHotelDto> hotelList = hotelService.findByIds(command.getManageHotelList());
        List<ManageTradingCompaniesDto> tradingCompaniesList = tradingCompaniesService.findByIds(command.getManageTradingCompaniesList());
        List<ManageReportDto> reportList = reportService.findByIds(command.getManageReportList());

        service.create(new ManageEmployeeDto(
                command.getId(),
                command.getFirstName(),
                command.getLastName(),
                command.getLoginName(),
                command.getEmail(),
                command.getInnsistCode(),
                command.getPhoneExtension(),
                manageDepartmentGroupDto,
                command.getStatus(),
                permissionList,
                agencyList,
                hotelList,
                tradingCompaniesList,
                command.getUserType(),
                reportList
        ));
        this.producerReplicateManageEmployeeService.create(new ReplicateManageEmployeeKafka(command.getId(), command.getFirstName(), command.getLastName(), command.getEmail()));
    }
}
