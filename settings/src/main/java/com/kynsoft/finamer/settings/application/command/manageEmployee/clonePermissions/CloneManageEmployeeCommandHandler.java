package com.kynsoft.finamer.settings.application.command.manageEmployee.clonePermissions;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CloneManageEmployeeCommandHandler implements ICommandHandler<CloneManageEmployeeCommand> {

    private final IManageEmployeeService service;

    public CloneManageEmployeeCommandHandler(IManageEmployeeService service) {
        this.service = service;
    }

    @Override
    public void handle(CloneManageEmployeeCommand command) {
        ManageEmployeeDto sourceEmployee = this.service.findById(command.getId());
        List<ManageEmployeeDto> targetEmployees = this.service.finAllByIds(command.getTargetEmployees());

        for (ManageEmployeeDto targetEmployee : targetEmployees){
            targetEmployee.setManagePermissionList(new ArrayList<>(sourceEmployee.getManagePermissionList()));
            targetEmployee.setManageAgencyList(new ArrayList<>(sourceEmployee.getManageAgencyList()));
            targetEmployee.setManageHotelList(new ArrayList<>(sourceEmployee.getManageHotelList()));
            targetEmployee.setManageTradingCompaniesList(new ArrayList<>(sourceEmployee.getManageTradingCompaniesList()));
            targetEmployee.setManageReportList(new ArrayList<>(sourceEmployee.getManageReportList()));
        }

        this.service.saveAll(targetEmployees);
    }
}
