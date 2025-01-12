package com.tailorw.tcaInnsist.infrastructure.model;

import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_connection", schema = "public")
public class ManageConnection {

    @Id
    private UUID id;
    private String server;
    private String port;
    private String dbName;
    private String userName;
    private String password;

    public ManageConnection(ManageConnectionDto dto){
        this.id = dto.getId();
        this.server = dto.getServer();
        this.port = dto.getPort();
        this.dbName = dto.getDbName();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
    }

    public ManageConnectionDto toAggregate(){
        return new ManageConnectionDto(
                this.id,
                this.server,
                this.port,
                this.dbName,
                this.userName,
                this.password
        );
    }
}
