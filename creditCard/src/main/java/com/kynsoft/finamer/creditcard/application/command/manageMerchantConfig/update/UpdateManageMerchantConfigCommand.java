package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import lombok.Data;
import java.util.UUID;

@Data
public class UpdateManageMerchantConfigCommand implements ICommand {

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
    private String merchantNumber;
    private String merchantTerminal;

    public UpdateManageMerchantConfigCommand(UUID id, UUID manageMerchant, String url, String altUrl, String successUrl, String errorUrl, String declinedUrl, String merchantType, String name, Method method, String institutionCode, String merchantNumber, String merchantTerminal) {
        this.id = id;
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
        this.merchantNumber = merchantNumber;
        this.merchantTerminal = merchantTerminal;
    }

    public static UpdateManageMerchantConfigCommand fromRequest(UpdateManageMerchantConfigRequest request, UUID id) {
        return new UpdateManageMerchantConfigCommand(
                id, 
                request.getManageMerchant(), 
                request.getUrl(), 
                request.getAltUrl(), 
                request.getSuccessUrl(), 
                request.getErrorUrl(), 
                request.getDeclinedUrl(),
                request.getMerchantType(), 
                request.getName(), 
                request.getMethod(),
                request.getInstitutionCode(),
                request.getMerchantNumber(),
                request.getMerchantTerminal()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManageMerchantConfigMessage(id);
    }
}
