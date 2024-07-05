package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageAttachmentTypeDto;
import jakarta.persistence.*;
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
@Table(name = "manage_attachment_type")
public class ManageAttachmentType implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(unique = true)
    private String code;
    private String name;

    public ManageAttachmentType(ManageAttachmentTypeDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
    }

    public ManageAttachmentTypeDto toAggregate(){
        return new ManageAttachmentTypeDto(
                id, code, name
        );
    }

}
