package com.tailorw.finamer.scheduler.domain.dto;

import com.tailorw.finamer.scheduler.infrastructure.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessProcessDto {
    private UUID id;
    private String code;
    private String processName;
    private String description;
    private Status status;
    private LocalDateTime updatedAt;
    public LocalDateTime deletedAt;
}
