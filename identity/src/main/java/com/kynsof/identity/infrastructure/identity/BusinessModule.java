package com.kynsof.identity.infrastructure.identity;

import com.kynsof.identity.domain.dto.BusinessModuleDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "business_module")
@Getter
@Setter
@NoArgsConstructor
public class BusinessModule {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id")
    private Business business;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private ModuleSystem module;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private Boolean deleted = false;

    public BusinessModule(Business business, ModuleSystem module) {
        this.business = business;
        this.module = module;
    }

    public BusinessModule(BusinessModuleDto businessModuleDto) {
        this.id = businessModuleDto.getId();
        this.business = new Business(businessModuleDto.getBusiness());
        this.module = new ModuleSystem(businessModuleDto.getModule());
    }

    public BusinessModuleDto toAggregate () {
        return new BusinessModuleDto(
                id, 
                business.toAggregate(), 
                module.toAggregate()
        );
    }

}
