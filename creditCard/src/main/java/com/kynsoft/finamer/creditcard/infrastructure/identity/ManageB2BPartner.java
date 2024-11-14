package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne()
    @JoinColumn(name = "b2b_partner_type_id", nullable = false)
    private ManageB2BPartnerType b2bPartnerType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

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
        this.b2bPartnerType = Objects.nonNull(dto.getB2BPartnerTypeDto()) ? new ManageB2BPartnerType(dto.getB2BPartnerTypeDto()) : null;
    }

    public ManagerB2BPartnerDto toAggregate() {
        return new ManagerB2BPartnerDto(
                id, code, name, description, status, url, ip, userName, password, token,
                b2bPartnerType != null ? b2bPartnerType.toAggregate() : null);
    }

}
