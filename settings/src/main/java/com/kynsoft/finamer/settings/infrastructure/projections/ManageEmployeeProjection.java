package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ManageEmployeeProjection {

    @Getter
    private UUID id;

    @Getter
    private ManageDepartmentGroupProjection departmentGroup;

    @Getter
    private Status status;

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

    @Getter
    private List<ManageAgencyProjection> manageAgencyList;

    @Getter
    private List<ManagePermissionProjection> managePermissionList;

    @Getter
    private List<ManageHotelProjection> manageHotelList;

    @Getter
    private List<ManageTradingCompanyProjection> manageTradingCompaniesList;
    //private List<ManageReport> manageReportList;

    @Getter
    private UserType userType;

    public ManageEmployeeProjection(UUID id,
                                    ManageDepartmentGroupProjection departmentGroup,
                                    Status status,
                                    String firstName,
                                    String lastName,
                                    String loginName,
                                    String email,
                                    String innsistCode,
                                    String phoneExtension,
                                    List<ManageAgencyProjection> manageAgencyList,
                                    List<ManagePermissionProjection> managePermissionList,
                                    List<ManageHotelProjection> manageHotelList,
                                    List<ManageTradingCompanyProjection> manageTradingCompaniesList,
                                    // List<ManageReport> manageReportList;
                                    UserType userType){

        this.id = id;
        this.departmentGroup = departmentGroup;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.loginName = loginName;
        this.email = email;
        this.innsistCode = innsistCode;
        this.phoneExtension = phoneExtension;
        this.manageAgencyList = manageAgencyList;
        this.managePermissionList = managePermissionList;
        this.manageHotelList = manageHotelList;
        this.manageTradingCompaniesList = manageTradingCompaniesList;
        this.userType = userType;
    }

}
