package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_b2b_partner")
public class ManageB2BPartner implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;
    
    private String name;
    private String description;
    private String url;
    private String ip;

    private String userName;
    private String password;
    private String token;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_partner_type_id", nullable = false)
    private ManageB2BPartnerType b2bPartnerType;

    @Column(nullable = true)
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;

    public ManageB2BPartner(ManagerB2BPartnerDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.url = dto.getUrl();
        this.ip = dto.getIp();
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.token = dto.getToken();
        this.b2bPartnerType = new ManageB2BPartnerType(dto.getB2BPartnerTypeDto());
    }

    public ManagerB2BPartnerDto toAggregate() {
        return new ManagerB2BPartnerDto(id, code, name, description, status, url, ip, userName, password,
                token,b2bPartnerType.getId(), b2bPartnerType.toAggregate());
    }

}
