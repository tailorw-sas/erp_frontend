package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ImportRoomRateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "import_room_rate", indexes = {
        @Index(name = "idx_import_process", columnList = "import_process_id"),
        @Index(name = "idx_roomrate", columnList = "roomRate_id"),
        @Index(name = "idx_import_process_roomrate", columnList = "import_process_id, roomRate_id")
})
public class ImportRoomRate {

    @Id
    private UUID Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "import_process_id")
    private ImportProcess importProcess;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_rate_id")
    private RoomRate roomRate;

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    public LocalDateTime updatedAt;

    public ImportRoomRate(ImportRoomRateDto dto){
        this.Id = dto.getId();
        this.importProcess = Objects.nonNull(dto.getImportProcess()) ? new ImportProcess(dto.getImportProcess()) : null;
        this.roomRate = Objects.nonNull(dto.getRoomRate()) ? new RoomRate(dto.getRoomRate()) : null;
        this.errorMessage = dto.getErrorMessage();
        this.updatedAt = dto.getUpdatedAt();
    }

    public ImportRoomRateDto toAggregate(){
        return new ImportRoomRateDto(
                this.Id,
                Objects.nonNull(this.importProcess) ? this.importProcess.toAggregate() : null,
                Objects.nonNull(this.roomRate) ? this.roomRate.toAggregate() : null,
                this.errorMessage,
                this.updatedAt
        );
    }
}
