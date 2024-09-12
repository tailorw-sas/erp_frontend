package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigResponseDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerMerchantConfigResponse implements IResponse {
    private UUID id;
    private ManagerMerchantResponse manageMerchant;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private Method method;
    private String institutionCode;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String merchantNumber;
    private String merchantTerminal;


    public ManagerMerchantConfigResponse(ManagerMerchantConfigResponseDto dto) {
        this.id = dto.getId();
        this.manageMerchant = dto.getManagerMerchantDto() != null ? new ManagerMerchantResponse(dto.getManagerMerchantDto()) : null;
        this.url =dto.getUrl();
        this.altUrl = dto.getAltUrl();
        this.successUrl = dto.getSuccessUrl();
        this.declinedUrl = dto.getDeclinedUrl();
        this.errorUrl = dto.getErrorUrl();
        this.merchantType = dto.getMerchantType();
        this.name = dto.getName();
        this.method = dto.getMethod();
        this.institutionCode = dto.getInstitutionCode();
        this.createAt = dto.getCreatedAt();
        this.updateAt = dto.getUpdatedAt();
        this.merchantNumber = dto.getMerchantNumber();
        this.merchantTerminal = dto.getMerchantTerminal();
    }


}
