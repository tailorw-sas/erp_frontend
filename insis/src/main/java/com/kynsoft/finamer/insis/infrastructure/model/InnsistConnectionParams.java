package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "innsist_connection_params")
public class InnsistConnectionParams implements Serializable {
    @Id
    private UUID id;

    private String hostName;

    private int portNumber;

    private String dataBaseName;

    private String userName;

    private String password;

    private String description;

    private String status;

    private boolean deleted;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    //@OneToOne(mappedBy = "innsistConnectionParams", cascade = CascadeType.ALL)
    //private ManageTradingCompany tradingCompany;

    public InnsistConnectionParams(InnsistConnectionParamsDto dto){
        this.id = dto.getId();
        this.hostName = dto.getHostName();
        this.portNumber = dto.getPortNumber();;
        this.dataBaseName = dto.getDataBaseName();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.deleted = dto.isDeleted();
        this.updatedAt = dto.getUpdatedAt();
        //this.tradingCompany = dto.getManageTradingCompany() != null ? new ManageTradingCompany(dto.getManageTradingCompany()) : null;
    }

    public InnsistConnectionParamsDto toAggregate(){
        return new InnsistConnectionParamsDto(
                id,
                hostName,
                portNumber,
                dataBaseName,
                userName,
                password,
                description,
                status,
                deleted,
                updatedAt//,
                //tradingCompany != null ? tradingCompany.toAggregate() : null
        );
    }
}
