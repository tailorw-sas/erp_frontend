package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.InnsistTcaConfigurationPropertiesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InnsistTcaConfigurationProperties implements Serializable {
    private UUID id;
    private String tradingCompany;
    private String hotel;
    private String roomType;
    private String server;
    private int port;
    private String dbName;
    private String userName;
    private String password;

    public InnsistTcaConfigurationPropertiesDto toAggregate(){
        return new InnsistTcaConfigurationPropertiesDto(
                this.id,
                this.tradingCompany,
                this.hotel,
                this.roomType,
                this.server,
                this.port,
                this.dbName,
                this.userName,
                this.password
        );
    }
}
