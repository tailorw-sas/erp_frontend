package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_invoice_status")
public class ManageInvoiceStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;


    private String name;
    private Boolean showClone;

    public ManageInvoiceStatus(ManageInvoiceStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();

        this.name = dto.getName();
        this.showClone = dto.getShowClone();
    }


    public ManageInvoiceStatusDto toAggregate() {
        return new ManageInvoiceStatusDto(
                id, code, name, showClone
        );
    }
}
