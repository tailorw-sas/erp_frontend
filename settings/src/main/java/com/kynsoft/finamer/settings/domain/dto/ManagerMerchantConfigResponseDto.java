package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerMerchantConfigResponseDto {
    private UUID id;
    private ManagerMerchantDto managerMerchantDto;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    @Enumerated(EnumType.STRING)
    private Method method;
    private String institutionCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
