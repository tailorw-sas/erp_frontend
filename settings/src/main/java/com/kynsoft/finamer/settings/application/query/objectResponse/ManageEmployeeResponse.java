package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyBasicResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup.ManageHotelBasicResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageTradingCompanies.ManageTradingCompaniesBasicResponse;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeResponse implements IResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String loginName;
    private String email;
    private String innsistCode;
    private String phoneExtension;
    private ManageDepartmentGroupResponse departmentGroup;
    private Status status;
    private List<ManagePermissionResponse> managePermissionList;
    private List<ManageAgencyBasicResponse> manageAgencyList;
    private List<ManageHotelBasicResponse> manageHotelList;
    private List<ManageTradingCompaniesBasicResponse> manageTradingCompaniesList;
    private UserType userType;
    private List<ManageReportResponse> manageReportList;

    public ManageEmployeeResponse(ManageEmployeeDto dto) {
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.loginName = dto.getLoginName();
        this.email = dto.getEmail();
        this.innsistCode = dto.getInnsistCode();
        this.phoneExtension = dto.getPhoneExtension();
        this.departmentGroup = dto.getDepartmentGroup() != null ? new ManageDepartmentGroupResponse(dto.getDepartmentGroup()) : null;
        this.status = dto.getStatus();
        this.managePermissionList = dto.getManagePermissionList() != null ? dto.getManagePermissionList().stream().map(ManagePermissionResponse::new).collect(Collectors.toList()) : null;
        this.manageAgencyList = dto.getManageAgencyList() != null ? dto.getManageAgencyList().stream().map(ManageAgencyBasicResponse::new).collect(Collectors.toList()) : null;
        this.manageHotelList = dto.getManageHotelList() != null ? dto.getManageHotelList().stream().map(ManageHotelBasicResponse::new).collect(Collectors.toList()) : null;
        this.manageTradingCompaniesList = dto.getManageTradingCompaniesList() != null ? dto.getManageTradingCompaniesList().stream().map(ManageTradingCompaniesBasicResponse::new).collect(Collectors.toList()) : null;
        this.userType = dto.getUserType();
        this.manageReportList = dto.getManageReportList() != null ? dto.getManageReportList().stream().map(ManageReportResponse::new).collect(Collectors.toList()) : null;
    }
}