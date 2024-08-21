package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.status.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "db_conection")
public class DBConection implements Serializable {

    @Id
    private UUID id;

    private String url;
    private String code;
    private String name;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public DBConection(DBConectionDto dto){
        this.id = dto.getId();
        this.url = dto.getUrl();
        this.username = dto.getUsername();
        this.password = dto.getPassword();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }

    public DBConectionDto toAggregate(){
        return new DBConectionDto(
                id, url, username, password,code, name, status
        );
    }
}
