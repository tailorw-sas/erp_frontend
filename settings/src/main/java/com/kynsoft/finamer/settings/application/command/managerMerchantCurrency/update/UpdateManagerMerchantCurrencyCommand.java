package com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerMerchantCurrencyCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private Double value;
    private String description;

    public UpdateManagerMerchantCurrencyCommand(UUID id, UUID managerMerchant, UUID managerCurrency, Double value, String description) {
        this.id = id;
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.value = value;
        this.description = description;
    }

    public static UpdateManagerMerchantCurrencyCommand fromRequest(UpdateManagerMerchantCurrencyRequest request, UUID id) {
        return new UpdateManagerMerchantCurrencyCommand(
                id,
                request.getManagerMerchant(),
                request.getManagerCurrency(), 
                request.getValue(),
                request.getDescription()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerMerchantCurrencyMessage(id);
    }
}
