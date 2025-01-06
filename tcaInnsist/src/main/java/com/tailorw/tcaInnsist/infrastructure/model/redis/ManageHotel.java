package com.tailorw.tcaInnsist.infrastructure.model.redis;

import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@RedisHash("tca_hotel")
public class ManageHotel {

    @Id
    private UUID id;

    @Indexed
    private String code;
    private String name;
    private String roomType;
    private UUID tradingCompanyId;

    public ManageHotelDto toAggregate(){
        return new ManageHotelDto(
                this.id,
                this.code,
                this.name,
                this.roomType,
                this.tradingCompanyId
        );
    }

    public ManageHotel(ManageHotelDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.roomType = dto.getRoomType();
        this.tradingCompanyId = dto.getTradingCompanyId();
    }
}
