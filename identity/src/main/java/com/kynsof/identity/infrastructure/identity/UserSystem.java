package com.kynsof.identity.infrastructure.identity;

import com.kynsof.identity.domain.dto.UserStatus;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.share.core.domain.EUserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
//@Audited
//@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_system")
public class UserSystem implements Serializable {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String userName;
    @Column(unique = true)
    private String email;
    private String name;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Enumerated(EnumType.STRING)
    private EUserType userType;
    private String image;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Column(nullable = true)
    private UUID selectedBusiness;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserPermissionBusiness> userRolesClinics = new HashSet<>();


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UserSystem(UserSystemDto dto) {
        this.id = dto.getId();
        this.userName = dto.getUserName();
        this.email = dto.getEmail();
        this.name = dto.getName();
        this.lastName = dto.getLastName();
        this.status = dto.getStatus();
        this.userType = dto.getUserType() != null ? dto.getUserType() : EUserType.UNDEFINED;
        this.image = dto.getImage() != null ? dto.getImage() : null;
        this.selectedBusiness = dto.getSelectedBusiness() != null ? dto.getSelectedBusiness() : null;
    }

    public UserSystemDto toAggregate() {
        UserSystemDto dto = new UserSystemDto(this.id, this.userName, this.email, this.name, this.lastName, this.status, this.image);
        dto.setUserType(userType);
        dto.setImage(image);
        dto.setSelectedBusiness(selectedBusiness);
        dto.setCreatedAt(createdAt.toLocalDate());
        return dto;
    }
}
