package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "import_process")
public class ImportProcess {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ImportProcessStatus status;

    private LocalDate importDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime completedAt;

    private int totalBooking;

    private UUID userId;

    @Column(nullable = true)
    private int totalSuccessful;

    @Column(nullable = true)
    private int  totalFailed;

    public ImportProcess(ImportProcessDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.importDate = dto.getImportDate();
        this.completedAt = dto.getCompletedAt();
        this.totalBooking = dto.getTotalBookings();
        this.userId = dto.getUserId();
        this.totalSuccessful = dto.getTotalSuccessful();
        this.totalFailed = dto.getTotalFailed();
    }

    public ImportProcessDto toAggregate(){
        return new ImportProcessDto(
                id,
                status,
                importDate,
                completedAt,
                totalBooking,
                userId,
                totalSuccessful,
                totalFailed
        );
    }
}
