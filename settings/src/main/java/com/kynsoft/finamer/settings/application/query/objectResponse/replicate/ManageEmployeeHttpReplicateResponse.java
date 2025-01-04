package com.kynsoft.finamer.settings.application.query.objectResponse.replicate;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeHttpReplicateResponse implements IResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String loginName;
    private String email;
    private String innsistCode;
    private String phoneExtension;
    private Status status;
    private UserType userType;
    
    public ManageEmployeeHttpReplicateResponse(ManageEmployeeDto dto) {
        this.id = dto.getId();
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.loginName = dto.getLoginName();
        this.email = dto.getEmail();
        this.innsistCode = dto.getInnsistCode();
        this.phoneExtension = dto.getPhoneExtension();
        this.status = dto.getStatus();
        this.userType = dto.getUserType();
    }
}