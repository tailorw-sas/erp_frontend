package com.tailorw.tcaInnsist.infrastructure.model;

import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "manage_trading_company", schema = "public")
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
