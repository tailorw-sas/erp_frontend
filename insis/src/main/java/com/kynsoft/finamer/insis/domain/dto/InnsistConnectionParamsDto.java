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
public class InnsistConnectionParamsDto {

    private UUID id;
    private String hostName;
    private int portNumber;
    private String databaseName;
    private String userName;
    private String password;
    private String description;
    private String status;
    private boolean deleted;
    private LocalDateTime updatedAt;
}
