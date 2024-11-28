package com.kynsoft.finamer.creditcard.application.command.hotelPayment.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateHotelPaymentMessage implements ICommandMessage {

    private final UUID id;

    private final Long hotelPaymentId;

    private final String command = "CREATE_HOTEL_PAYMENT";

}
