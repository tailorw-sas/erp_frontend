package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_report_param_type")
public class ManageReportParamType {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    private String label;

    private Boolean hotel;

    private String source;

    public ManageReportParamType(ManageReportParamTypeDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.label = dto.getLabel();
        this.hotel = dto.getHotel();
        this.source = dto.getSource();
    }

    public ManageReportParamTypeDto toAggregate(){
        return new ManageReportParamTypeDto(
                id, status, name,label,hotel,source
        );
    }
}
