package com.kynsoft.finamer.settings.application.command.manageMerchantCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManageMerchantCurrencyCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private String value;
    private String description;
    private Status status;

    public CreateManageMerchantCurrencyCommand(UUID managerMerchant, UUID managerCurrency, String value, String description, Status status) {
        this.id = UUID.randomUUID();
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.value = value;
        this.description = description;
        this.status = status;
    }

    public static CreateManageMerchantCurrencyCommand fromRequest(CreateManageMerchantCurrencyRequest request) {
        return new CreateManageMerchantCurrencyCommand(
                request.getManagerMerchant(),
                request.getManagerCurrency(),
                request.getValue(),
                request.getDescription(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantCurrencyMessage(id);
    }
}
