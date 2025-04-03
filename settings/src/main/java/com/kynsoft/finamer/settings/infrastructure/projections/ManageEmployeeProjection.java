package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ManageEmployeeProjection {

    @Getter
    private UUID id;

    @Getter
    private ManageDepartmentGroupProjection departmentGroup;

    @Getter
    private String status;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Getter
    private String loginName;

    @Getter
    private String email;

    @Getter
    private String innsistCode;

    @Getter
    private String phoneExtension;
    //private List<ManagePermission> managePermissionList;
    //private List<ManageAgency> manageAgencyList;
    //private List<ManageHotel> manageHotelList;
    //private List<ManageTradingCompanies> manageTradingCompaniesList;
    //private List<ManageReport> manageReportList;

    @Getter
    private String userType;

    public ManageEmployeeProjection(UUID id,
                                     ManageDepartmentGroupProjection departmentGroup,
                                     String status,
                                     String firstName,
                                     String lastName,
                                     String loginName,
                                     String email,
                                     String innsistCode,
                                     String phoneExtension,
                                    // List<ManagePermission> managePermissionList;
                                    // List<ManageAgency> manageAgencyList;
                                    // List<ManageHotel> manageHotelList;
                                    // List<ManageTradingCompanies> manageTradingCompaniesList;
                                    // List<ManageReport> manageReportList;
                                     String userType){

        this.id = id;
        this.departmentGroup = departmentGroup;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.email = email;
        this.innsistCode = innsistCode;
        this.phoneExtension = phoneExtension;
        this.userType = userType;
    }

}
