package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateManageMerchantHotelEnrolleCommand implements ICommand {

    private UUID id;
    private UUID manageMerchant;
    private UUID manageHotel;
    private String enrolle;
    private String status;

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageMerchantHotelEnrolleMessage(id);
    }
}
