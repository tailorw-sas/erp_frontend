package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_trading_company")
public class ManageTradingCompany implements Serializable {
    @Id
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "company")
    private String company;

    @Column(name = "innsist_code")
    private String innsistCode;

    @Column(name = "status")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "manage_innsist_connection_id", unique = true, nullable = true)
    private InnsistConnectionParams innsistConnectionParam;

    @Column(name = "has_connection", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean hasConnection;

    public ManageTradingCompany(ManageTradingCompanyDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.company = dto.getCompany();
        this.innsistCode = dto.getInnsistCode();
        this.status = dto.getStatus();
        this.updatedAt = dto.getUpdatedAt();
        this.innsistConnectionParam = dto.getInnsistConnectionParams() == null ? null : new InnsistConnectionParams(dto.getInnsistConnectionParams());
        this.hasConnection = dto.isHasConnection();
    }

    public ManageTradingCompanyDto toAggregate(){
        return new ManageTradingCompanyDto(
                id,
                code,
                company,
                innsistCode,
                status,
                updatedAt,
                innsistConnectionParam != null ? innsistConnectionParam.toAggregate() : null,
                hasConnection
        );
    }
}
