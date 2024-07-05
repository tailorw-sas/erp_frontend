package com.kynsoft.finamer.settings.application.command.manageEmployee.update;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;

import java.util.List;
import java.util.UUID;

import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateManageEmployeeRequest {
    private String firstName;
    private String lastName;
    private String loginName;
    private String email;
    private String innsistCode;
    private String phoneExtension;
    private UUID departmentGroup;
    private Status status;
    private List<UUID> managePermissionList;
    private List<UUID> manageAgencyList;
    private List<UUID> manageHotelList;
    private List<UUID> manageTradingCompaniesList;
    private UserType userType;
    private List<UUID> manageReportList;
}
