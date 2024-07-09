package com.kynsoft.finamer.invoicing.application.command.manageBooking.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateBookingCommand implements ICommand {

        private UUID id;
        private LocalDateTime hotelCreationDate;
        private LocalDateTime bookingDate;
        private LocalDateTime checkIn;
        private LocalDateTime checkOut;

        private String hotelBookingNumber;
        private String fullName;
        private String firstName;
        private String lastName;
        private Double invoiceAmount;
        private String roomNumber;
        private String couponNumber;
        private Integer adults;
        private Integer children;
        private Double rateAdult;
        private Double rateChild;
        private String hotelInvoiceNumber;
        private String folioNumber;
        private Double hotelAmount;
        private String description;

        private UUID invoice;
        private UUID ratePlan;
        private UUID nightType;
        private UUID roomType;
        private UUID roomCategory;

        public CreateBookingCommand(UUID id, LocalDateTime hotelCreationDate, LocalDateTime bookingDate,
                        LocalDateTime checkIn, LocalDateTime checkOut, String hotelBookingNumber, String fullName,

                        String firstName,
                        String lastName,
                        Double invoiceAmount, String roomNumber, String couponNumber, Integer adults, Integer children,
                        Double rateAdult, Double rateChild, String hotelInvoiceNumber, String folioNumber,
                        Double hotelAmount,
                        String description, UUID invoice, UUID ratePlan, UUID nightType, UUID roomType,
                        UUID roomCategory) {
                this.id = id;
                this.hotelCreationDate = hotelCreationDate;
                this.bookingDate = bookingDate;
                this.checkIn = checkIn;
                this.checkOut = checkOut;
                this.hotelBookingNumber = hotelBookingNumber;
                this.fullName = fullName;
                this.invoiceAmount = invoiceAmount;
                this.roomNumber = roomNumber;
                this.couponNumber = couponNumber;
                this.adults = adults;
                this.children = children;
                this.rateAdult = rateAdult;
                this.rateChild = rateChild;
                this.hotelInvoiceNumber = hotelInvoiceNumber;
                this.folioNumber = folioNumber;
                this.hotelAmount = hotelAmount;
                this.description = description;
                this.invoice = invoice;
                this.ratePlan = ratePlan;
                this.nightType = nightType;
                this.roomType = roomType;
                this.firstName = firstName;
                this.lastName = lastName;
                this.roomCategory = roomCategory;

        }

        public static CreateBookingCommand fromRequest(CreateBookingRequest request) {
                return new CreateBookingCommand(
                                request.getId() == null ? UUID.randomUUID() : request.getId(),
                                request.getHotelCreationDate(),
                                request.getBookingDate(),
                                request.getCheckIn(),
                                request.getCheckOut(),
                                request.getHotelBookingNumber(),
                                request.getFullName(),
                                request.getFirstName(),
                                request.getLastName(),
                                request.getInvoiceAmount(),
                                request.getRoomNumber(),
                                request.getCouponNumber(),
                                request.getAdults(),
                                request.getChildren(),
                                request.getRateAdult(),
                                request.getRateChild(),
                                request.getHotelInvoiceNumber(),
                                request.getFolioNumber(),
                                request.getHotelAmount(),
                                request.getDescription(),
                                request.getInvoice() != null && !request.getInvoice().isEmpty()
                                                ? UUID.fromString(request.getInvoice())
                                                : null,
                                request.getRatePlan() != null && !request.getRatePlan().isEmpty()
                                                ? UUID.fromString(request.getRatePlan())
                                                : null,
                                request.getNightType() != null && !request.getNightType().isEmpty()
                                                ? UUID.fromString(request.getNightType())
                                                : null,
                                request.getRoomType() != null && !request.getRoomType().isEmpty()
                                                ? UUID.fromString(request.getRoomType())
                                                : null,
                                request.getRoomCategory() != null && !request.getRoomCategory().isEmpty()
                                                ? UUID.fromString(request.getRoomCategory())
                                                : null);
        }

        @Override
        public ICommandMessage getMessage() {
                return new CreateBookingMessage(id);
        }
}
