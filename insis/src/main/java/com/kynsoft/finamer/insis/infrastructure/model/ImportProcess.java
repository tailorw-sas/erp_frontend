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
    @Column(name = "status")
    private ImportProcessStatus status;

    @Column(name = "import_date")
    private LocalDate importDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at", nullable = true, updatable = true)
    private LocalDateTime completedAt;

    @Column(name = "total_booking")
    private int totalBooking;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "total_successful", nullable = true)
    private int totalSuccessful;

    @Column(name = "total_failed", nullable = true)
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
