package com.tailorw.tcaInnsist.infrastructure.model;

import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_hotel", schema = "public")
public class ManageHotel {

    @Id
    private UUID id;
    private String code;
    private String name;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "trading_company_id")
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
