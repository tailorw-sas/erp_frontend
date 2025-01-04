package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageContactDto;
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
@Table(name = "manage_contact")
public class ManageContact implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String code;

    private String description;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel manageHotel;

    private String email;

    private String phone;

    private Integer position;

    public ManageContact(ManageContactDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.manageHotel = new ManageHotel(dto.getManageHotel());
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        this.position = dto.getPosition();
    }

    public ManageContactDto toAggregate(){
        return new ManageContactDto(
                id, code, description, name,
                manageHotel != null ? manageHotel.toAggregate() : null,
                email, phone, position
        );
    }
}
