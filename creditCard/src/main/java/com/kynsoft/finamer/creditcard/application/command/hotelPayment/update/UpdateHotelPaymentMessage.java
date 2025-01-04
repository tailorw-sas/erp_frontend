package com.kynsoft.finamer.creditcard.application.command.hotelPayment.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateHotelPaymentMessage implements ICommandMessage {

    private final UUID id;

    private final Long hotelPaymentId;

    private final String command = "UPDATE_HOTEL_PAYMENT";

}
