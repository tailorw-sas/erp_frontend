package com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
public class CreateManyBookingsCommand implements ICommand {

    private List<CreateBookingCommand> bookings;

    public static CreateManyBookingsCommand fromRequest(CreateManyBookingRequest request) {
        return new CreateManyBookingsCommand(request.getBookings().stream()
                .map(e -> new CreateBookingCommand(e.getId() != null ? e.getId() : UUID.randomUUID(),
                        e.getHotelCreationDate(), e.getBookingDate(), e.getCheckIn(), e.getCheckOut(),
                        e.getHotelBookingNumber(), e.getFullName(), e.getFirstName(), e.getLastName(),
                        e.getInvoiceAmount(), e.getRoomNumber(),
                        e.getCouponNumber(), e.getAdults(), e.getChildren(), e.getRateAdult(), e.getRateChild(),
                        e.getHotelInvoiceNumber(), e.getFolioNumber(), e.getHotelAmount(), e.getDescription(),
                        e.getInvoice() != null && !e.getInvoice().isEmpty() ? UUID.fromString(e.getInvoice()) : null,
                        e.getRatePlan() != null && !e.getRatePlan().isEmpty() ? UUID.fromString(e.getRatePlan()) : null,
                        e.getNightType() != null && !e.getNightType().isEmpty() ? UUID.fromString(e.getNightType())
                                : null,
                        e.getRoomType() != null && !e.getRoomType().isEmpty() ? UUID.fromString(e.getRoomType()) : null,
                        e.getRoomCategory() != null && !e.getRoomCategory().isEmpty()
                                ? UUID.fromString(e.getRoomCategory())
                                : null))
                .collect(Collectors.toList()));
    }

    @Override
    public CreateManyBookingMessage getMessage() {
        return new CreateManyBookingMessage(bookings.stream().map(e -> e.getId()).collect(Collectors.toList()));
    }
}
