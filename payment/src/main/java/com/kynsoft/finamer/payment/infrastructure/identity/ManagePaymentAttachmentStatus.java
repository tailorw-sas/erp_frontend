package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_payment_attachment_status")
public class ManagePaymentAttachmentStatus {

    @Id
    @Column(name = "id")
    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean defaults;

    public ManagePaymentAttachmentStatus(ManagePaymentAttachmentStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
        this.defaults = dto.getDefaults();
    }

    public ManagePaymentAttachmentStatusDto toAggregate() {
        return new ManagePaymentAttachmentStatusDto(id, code, name, status, defaults);
    }
}
