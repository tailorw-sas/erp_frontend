package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageAgencyTypeDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_agency_type")
public class ManageAgencyType {

    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String status;
    private String name;

    public ManageAgencyType(ManageAgencyTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.name = dto.getName();
    }

    public ManageAgencyTypeDto toAggregate(){
        return new ManageAgencyTypeDto(
                id, code, status, name
        );
    }
}
