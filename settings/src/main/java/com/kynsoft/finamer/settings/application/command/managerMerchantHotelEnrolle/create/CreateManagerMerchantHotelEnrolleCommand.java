package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateManagerMerchantHotelEnrolleCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private UUID managerHotel;
    private String enrolle;
    private String key;
    private String description;
    private Status status;

    public CreateManagerMerchantHotelEnrolleCommand(UUID managerMerchant, UUID managerCurrency, UUID managerHotel, String enrolle, String key, String description, Status status) {
        this.id = UUID.randomUUID();
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.managerHotel = managerHotel;
        this.enrolle = enrolle;
        this.key = key;
        this.description = description;
        this.status = status;
    }

    public static CreateManagerMerchantHotelEnrolleCommand fromRequest(CreateManagerMerchantHotelEnrolleRequest request) {
        return new CreateManagerMerchantHotelEnrolleCommand(
                request.getManagerMerchant(),
                request.getManagerCurrency(),
                request.getManagerHotel(),
                request.getEnrrolle(),
                request.getKey(),
                request.getDescription(),
                request.getStatus()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManagerMerchantHotelEnrolleMessage(id);
    }
}
