package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateManageMerchantConfigCommand implements ICommand {

    private UUID id;
    private UUID manageMerchant;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private Method method;
    private String institutionCode;

    public CreateManageMerchantConfigCommand(UUID manageMerchant, String url, String altUrl, String successUrl, String errorUrl, String declinedUrl, String merchantType, String name, Method method, String institutionCode) {
        this.id = UUID.randomUUID();
        this.manageMerchant = manageMerchant;
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

    public static CreateManageMerchantConfigCommand fromRequest(CreateManageMerchantConfigRequest request) {
        return new CreateManageMerchantConfigCommand(
                request.getManageMerchant(), 
                request.getUrl(), 
                request.getAltUrl(), 
                request.getSuccessUrl(), 
                request.getErrorUrl(), 
                request.getDeclinedUrl(),
                request.getMerchantType(), 
                request.getName(), 
                request.getMethod(), 
                request.getInstitutionCode()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantConfigMessage(id);
    }
}
