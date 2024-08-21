package com.kynsoft.report.applications.query.dbconection.getById;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DBConectionResponse implements IResponse {

    private UUID id;
    private String url;
    private String username;
//    private String password;
    private String code;
    private String name;
    private Status status;

    public DBConectionResponse(DBConectionDto dto){
        this.id = dto.getId();
        this.url = dto.getUrl();
        this.username = dto.getUsername();
//        this.password = dto.getPassword();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }
}
