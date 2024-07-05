package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
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

//    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String description;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel manageHotel;

    private String email;

    private String phone;

    private Integer position;

    public ManageContact(ManageContactDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.status = dto.getStatus();
        this.description = dto.getDescription();
        this.name = dto.getName();
        this.manageHotel = new ManageHotel(dto.getManageHotel());
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        this.position = dto.getPosition();
    }

    public ManageContactDto toAggregate(){
        return new ManageContactDto(
                id, code, description, status, name,
                manageHotel != null ? manageHotel.toAggregate() : null,
                email, phone, position
        );
    }
}
