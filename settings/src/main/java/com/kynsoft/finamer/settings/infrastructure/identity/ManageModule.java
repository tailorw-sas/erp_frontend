package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ModuleDto;
import com.kynsoft.finamer.settings.domain.dto.ModuleStatus;
import com.kynsoft.finamer.settings.domain.dto.PermissionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_module")
public class ManageModule implements Serializable {
    @Id
    @Column(name = "id")
    protected UUID id;
    private String name;
    private String image;
    private String description;

    @Enumerated(EnumType.STRING)
    private ModuleStatus status;

    @Column(unique = true)
    private String code;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ManagePermission> permissions = new HashSet<>();

    public ManageModule(ModuleDto module) {
        this.id = module.getId();
        this.name = module.getName();
        this.image = module.getImage();
        this.description = module.getDescription();
        this.status = module.getStatus();
        this.code = module.getCode();
    }

    public ModuleDto toAggregate () {
        List<PermissionDto> p = new ArrayList<>();
        for (ManagePermission permission : permissions) {
            p.add(new PermissionDto(permission.getId(), permission.getCode(), permission.getDescription(), permission.getIsHighRisk(), permission.getStatus()));
        }

        return new ModuleDto(id, name, image, description, p, createdAt, status, code);
    }

}
