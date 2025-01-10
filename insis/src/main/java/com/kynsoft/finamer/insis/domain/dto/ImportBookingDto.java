package com.kynsoft.finamer.insis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportBookingDto {
    private UUID id;
    private ImportProcessDto importProcess;
    private BookingDto booking;
    private String errorMessage;
    public LocalDateTime updatedAt;

    public ImportBookingDto(UUID id, ImportProcessDto importProcess, BookingDto booking){
        this.id = id;
        this.importProcess = importProcess;
        this.booking = booking;
    }
}
