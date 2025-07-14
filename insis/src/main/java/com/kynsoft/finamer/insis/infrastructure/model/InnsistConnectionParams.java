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

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "port_number")
    private int portNumber;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "deleted")
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public InnsistConnectionParams(InnsistConnectionParamsDto dto){
        this.id = dto.getId();
        this.hostName = dto.getHostName();
        this.portNumber = dto.getPortNumber();;
        this.databaseName = dto.getDatabaseName();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.deleted = dto.isDeleted();
        this.updatedAt = dto.getUpdatedAt();
    }

    public InnsistConnectionParamsDto toAggregate(){
        return new InnsistConnectionParamsDto(
                id,
                hostName,
                portNumber,
                databaseName,
                userName,
                password,
                description,
                status,
                deleted,
                updatedAt
        );
    }
}
