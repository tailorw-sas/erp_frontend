package com.kynsoft.finamer.insis.infrastructure.model;

import com.kynsoft.finamer.insis.domain.dto.ImportBookingDto;
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
@Table(name = "import_booking", indexes = {
        @Index(name = "idx_import_process", columnList = "import_process_id"),
        @Index(name = "idx_booking", columnList = "booking_id"),
        @Index(name = "idx_import_process_booking", columnList = "import_process_id, booking_id")
})
public class ImportBooking {

    @Id
    private UUID Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "import_process_id")
    private ImportProcess importProcess;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    public LocalDateTime updatedAt;

    public ImportBooking(ImportBookingDto dto){
        this.Id = dto.getId();
        this.importProcess = Objects.nonNull(dto.getImportProcess()) ? new ImportProcess(dto.getImportProcess()) : null;
        this.booking = Objects.nonNull(dto.getBooking()) ? new Booking(dto.getBooking()) : null;
        this.errorMessage = dto.getErrorMessage();
        this.updatedAt = dto.getUpdatedAt();
    }

    public ImportBookingDto toAggregate(){
        return new ImportBookingDto(
                this.Id,
                Objects.nonNull(this.importProcess) ? this.importProcess.toAggregate() : null,
                Objects.nonNull(this.booking) ? this.booking.toAggregate() : null,
                this.errorMessage,
                this.updatedAt
        );
    }
}
