package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateManagerMerchantHotelEnrolleCommand implements ICommand {

    private UUID id;
    private UUID managerMerchant;
    private UUID managerHotel;
    private String enrolle;

    @Override
    public ICommandMessage getMessage() {
        return new UpdateManagerMerchantHotelEnrolleMessage(id);
    }
}
