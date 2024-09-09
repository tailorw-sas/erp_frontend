package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateManageMerchantConfigCommand implements ICommand {
    private UUID id;
    private UUID manageMerchantUuid;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private Method method;
    private String institutionCode;

    public UpdateManageMerchantConfigCommand(UUID id, UUID manageMerchantUuid, String url, String altUrl, String successUrl, String errorUrl, String declinedUrl, String merchantType, String name, Method method, String institutionCode) {
        this.id = id;
        this.manageMerchantUuid = manageMerchantUuid;
        this.url = url;
        this.altUrl = altUrl;
        this.successUrl = successUrl;
        this.errorUrl = errorUrl;
        this.declinedUrl = declinedUrl;
        this.merchantType = merchantType;
        this.name = name;
        this.method = method;
        this.institutionCode = institutionCode;
    }

    public static UpdateManageMerchantConfigCommand fromRequest(UpdateManageMerchantConfigRequest request,UUID id) {
        return new UpdateManageMerchantConfigCommand(
                id,request.getManageMerchantUuid(), request.getUrl(), request.getAltUrl(), request.getSuccessUrl(), request.getErrorUrl(), request.getDeclinedUrl(),
                request.getMerchantType(), request.getName(), request.getMethod(), request.getInstitutionCode()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageMerchantConfigMessage(id);
    }
}
