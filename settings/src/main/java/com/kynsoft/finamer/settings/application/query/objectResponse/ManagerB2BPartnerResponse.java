package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class ManagerB2BPartnerResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private String url;
    private String ip;
    private String userName;
    private String password;
    private String token;
    private ManageB2BPartnerTypeResponse b2BPartnerType;

    public ManagerB2BPartnerResponse(ManagerB2BPartnerDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.url = dto.getUrl();
        this.ip = dto.getIp();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.token = dto.getToken();
        this.b2BPartnerType = new ManageB2BPartnerTypeResponse(dto.getB2BPartnerTypeDto());
    }

}
