package com.kynsoft.finamer.settings.application.command.manageMerchantCurrency.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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
    private Status status;

    public UpdateManagerMerchantCurrencyCommand(UUID id, UUID managerMerchant, UUID managerCurrency, Double value, String description, Status status) {
        this.id = id;
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.value = value;
        this.description = description;
        this.status = status;
    }

    public static UpdateManagerMerchantCurrencyCommand fromRequest(UpdateManagerMerchantCurrencyRequest request, UUID id) {
        return new UpdateManagerMerchantCurrencyCommand(
                id,
                request.getManagerMerchant(),
                request.getManagerCurrency(), 
                request.getValue(),
                request.getDescription(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerMerchantCurrencyMessage(id);
    }
}
