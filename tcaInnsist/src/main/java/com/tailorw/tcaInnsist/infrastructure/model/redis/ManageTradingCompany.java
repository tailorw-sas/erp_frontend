package com.tailorw.tcaInnsist.infrastructure.model.redis;

import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RedisHash("tca_tradingCompany")
public class ManageTradingCompany {

    @Id
    private UUID id;
    private String code;
    private String name;
    private UUID connectionId;

    public ManageTradingCompany(ManageTradingCompanyDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.connectionId = dto.getConnectionId();
    }

    public ManageTradingCompanyDto toAggregate(){
        return new ManageTradingCompanyDto(
                this.id,
                this.code,
                this.name,
                this.connectionId
        );
    }
}
