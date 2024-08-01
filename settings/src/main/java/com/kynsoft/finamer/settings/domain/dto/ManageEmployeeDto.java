package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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
    private String phoneExtension;
    private ManageDepartmentGroupDto departmentGroup;
    private Status status;
    private List<PermissionDto> managePermissionList;
    private List<ManageAgencyDto> manageAgencyList;
    private List<ManageHotelDto> manageHotelList;
    private List<ManageTradingCompaniesDto> manageTradingCompaniesList;
    private UserType userType;
    private List<ManageReportDto> manageReportList;

}