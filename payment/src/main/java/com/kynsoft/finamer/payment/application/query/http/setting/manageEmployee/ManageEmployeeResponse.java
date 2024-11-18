package com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee;

import java.io.Serializable;
import java.util.UUID;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeResponse  implements IResponse, Serializable {
    private UUID id;
    private String firstName;
    private String lastName;
    private String loginName;
    private String email;
    private String innsistCode;
    private String phoneExtension;
    private String status;
    private String userType;

    public ManageEmployeeDto createObject() {
        return new ManageEmployeeDto(id, firstName, lastName, email);
    }
}
