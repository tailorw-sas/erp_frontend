package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import jakarta.persistence.*;
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
@Table(name = "manage_payment_status")
public class ManagePaymentStatus {
    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean applied;

    public ManagePaymentStatus(ManagePaymentStatusDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.applied = dto.getApplied();
    }
    
    public ManagePaymentStatusDto toAggregate(){
        return new ManagePaymentStatusDto(id, code, name, status, applied);
    }
}
