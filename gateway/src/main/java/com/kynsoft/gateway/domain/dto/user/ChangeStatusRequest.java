package com.kynsoft.gateway.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ChangeStatusRequest {
    public UUID userId;
    public String status;
}
