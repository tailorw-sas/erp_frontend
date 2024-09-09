package com.kynsoft.finamer.settings.domain.dto.me;

import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerMerchantConfigResponseDto {
    private UUID uuid;
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
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
