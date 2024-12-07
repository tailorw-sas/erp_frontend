package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_trading_companies")
public class ManageTradingCompanies implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;

    private Boolean isApplyInvoice;
    private String company;
    private String cif;
    private String address;
    private String status;

    @Column(columnDefinition = "serial", name = "autogen_code")
    @Generated(event = EventType.INSERT)
    private Long autogen_code;

    public ManageTradingCompanies(ManageTradingCompaniesDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.isApplyInvoice = dto.getIsApplyInvoice();
        this.cif = dto.getCif();
        this.address = dto.getAddress();
        this.company = dto.getCompany();
        this.status = dto.getStatus();
    }

    public ManageTradingCompaniesDto toAggregate() {
        return new ManageTradingCompaniesDto(
                id, code, isApplyInvoice, autogen_code, this.cif, this.address, this.company, status);
    }
}
