package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.me.ManagerMerchantConfigResponseDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerMerchant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerMerchantConfigResponse implements IResponse {
    private UUID uuid;
    private ManagerMerchant managerMerchant;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private Method method;
    private String institutionCode;
    private Timestamp createAt;
    private Timestamp updateAt;

    public ManagerMerchantConfigResponse(ManagerMerchantConfigResponseDto dto) {
        this.uuid = dto.getUuid();
        this.managerMerchant = new ManagerMerchant(dto.getManagerMerchantDto());
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
    }


}
