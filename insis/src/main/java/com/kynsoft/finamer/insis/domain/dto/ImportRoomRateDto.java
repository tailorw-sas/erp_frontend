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
public class ImportRoomRateDto {
    private UUID id;
    private ImportProcessDto importProcess;
    private RoomRateDto roomRate;
    private String errorMessage;
    private LocalDateTime updatedAt;

    public ImportRoomRateDto(UUID id, ImportProcessDto importProcess, RoomRateDto roomRate){
        this.id = id;
        this.importProcess = importProcess;
        this.roomRate = roomRate;
    }
}
