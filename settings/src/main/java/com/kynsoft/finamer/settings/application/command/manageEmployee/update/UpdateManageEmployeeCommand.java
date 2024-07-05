package com.kynsoft.finamer.settings.application.command.manageEmployee.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManageEmployeeCommand implements ICommand {

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

    public static UpdateManageEmployeeCommand fromRequest(UpdateManageEmployeeRequest request, UUID id) {
        return new UpdateManageEmployeeCommand(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getLoginName(),
                request.getEmail(),
                request.getInnsistCode(),
                request.getPhoneExtension(),
                request.getDepartmentGroup(),
                request.getStatus(),
                request.getManagePermissionList(),
                request.getManageAgencyList(),
                request.getManageHotelList(),
                request.getManageTradingCompaniesList(),
                request.getUserType(),
                request.getManageReportList()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageEmployeeMessage(id);
    }
}
