package com.kynsoft.finamer.settings.application.command.manageEmployee.create;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageEmployeeRequest {
    private UUID id;
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
