package com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerMerchantCurrencyCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private Double value;
    private String description;

    public CreateManagerMerchantCurrencyCommand(UUID managerMerchant, UUID managerCurrency, Double value, String description) {
        this.id = UUID.randomUUID();
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.value = value;
        this.description = description;
    }

    public static CreateManagerMerchantCurrencyCommand fromRequest(CreateManagerMerchantCurrencyRequest request) {
        return new CreateManagerMerchantCurrencyCommand(
                request.getManagerMerchant(),
                request.getManagerCurrency(),
                request.getValue(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerMerchantCurrencyMessage(id);
    }
}
