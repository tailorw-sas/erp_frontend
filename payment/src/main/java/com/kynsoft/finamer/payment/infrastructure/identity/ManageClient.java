package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
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
@Table(name = "manage_client")
public class ManageClient implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    private String code;

    private String name;

    private String status;

    public ManageClient(ManageClientDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.status = dto.getStatus();
    }

    public ManageClientDto toAggregate() {
        return new ManageClientDto(id, code, name, status);
    }

}