package com.kynsoft.finamer.insis.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InnsistHotelRoomTypeDto {
    public UUID id;
    public ManageHotelDto hotel;
    private String roomTypePrefix;
    private String status;
    private LocalDateTime updatedAt;
    private boolean deleted;
}
