package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateManagerMerchantHotelEnrolleCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerCurrency;
    private UUID managerHotel;
    private String enrolle;
    private String key;
    private String description;
    private Status status;

    public UpdateManagerMerchantHotelEnrolleCommand(UUID id, UUID managerMerchant, UUID managerCurrency, UUID managerHotel, String enrolle, String key, String description, Status status) {
        this.id = id;
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.managerHotel = managerHotel;
        this.enrolle = enrolle;
        this.key = key;
        this.description = description;
        this.status = status;
    }

    public static UpdateManagerMerchantHotelEnrolleCommand fromRequest(UpdateManagerMerchantHotelEnrolleRequest request, UUID id) {
        return new UpdateManagerMerchantHotelEnrolleCommand(
                id,
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
        return new UpdateManagerMerchantHotelEnrolleMessage(id);
    }
}
