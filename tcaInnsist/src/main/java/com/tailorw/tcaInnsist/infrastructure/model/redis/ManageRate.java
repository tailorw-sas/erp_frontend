package com.tailorw.tcaInnsist.infrastructure.model.redis;

import com.tailorw.tcaInnsist.domain.dto.ManageRateDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash("tca_manageRate")
public class ManageRate {

    @Id
    public String id;
    public LocalDate invoiceDate;
    public String hotel;
    public String reservationNumber;
    public String couponNumber;
    public String renewalNumber;
    public String hash;

    public ManageRate(ManageRateDto dto){
        this.id = dto.getId();
        this.invoiceDate = dto.getInvoiceDate();
        this.hotel = dto.getHotel();
        this.reservationNumber = dto.getReservationNumber();
        this.couponNumber = dto.getCouponNumber();
        this.renewalNumber = dto.getRenewalNumber();
        this.hash = dto.getHash();
    }

    public ManageRateDto toAggregate(){
        return new ManageRateDto(
                this.id,
                this.invoiceDate,
                this.hotel,
                this.renewalNumber,
                this.couponNumber,
                this.renewalNumber,
                this.hash
        );
    }
}
