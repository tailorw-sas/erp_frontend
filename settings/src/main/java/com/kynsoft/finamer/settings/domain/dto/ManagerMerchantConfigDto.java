package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManagerMerchantConfigDto {
    private UUID id;
    private ManagerMerchantDto managerMerchantDto;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private Method method;
    private String institutionCode;
    private String merchantNumber;
    private String merchantTerminal;
}
