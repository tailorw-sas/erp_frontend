package com.kynsoft.finamer.payment.application.command.invoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CreateInvoiceCommandHandler implements ICommandHandler<CreateInvoiceCommand> {

    private final IManageInvoiceService invoiceService;
    private final IManageBookingService bookingService;
    private final IManageHotelService hotelService;
    private final IManageAgencyService manageAgencyService;

    public CreateInvoiceCommandHandler(IManageInvoiceService invoiceService,
            IManageBookingService bookingService,
            IManageHotelService hotelService,
            IManageAgencyService manageAgencyService) {
        this.invoiceService = invoiceService;
        this.bookingService = bookingService;
        this.hotelService = hotelService;
        this.manageAgencyService = manageAgencyService;
    }

    @Override
    public void handle(CreateInvoiceCommand command) {
        ManageHotelDto hotelDto = this.hotelService.findById(command.getBookingHttp().getInvoice().getHotel());
        ManageAgencyDto agencyDto = this.manageAgencyService.findById(command.getBookingHttp().getInvoice().getAgency());
        List<ManageBookingDto> bookingDtos = new ArrayList<>();
        createBookingList(command, bookingDtos);

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                command.getBookingHttp().getInvoice().getId(),
                command.getBookingHttp().getInvoice().getInvoiceId(),
                command.getBookingHttp().getInvoice().getInvoiceNo(),
                deleteHotelInfo(command.getBookingHttp().getInvoice().getInvoiceNumber()),
                EInvoiceType.valueOf(command.getBookingHttp().getInvoice().getInvoiceType()),
                command.getBookingHttp().getInvoice().getInvoiceAmount(),
                bookingDtos,
                command.getBookingHttp().getInvoice().getHasAttachment(), //!= null ? objKafka.getHasAttachment() : false
                command.getBookingHttp().getInvoice().getInvoiceParent() != null ? this.invoiceService.findById(command.getBookingHttp().getInvoice().getInvoiceParent()) : null,
                LocalDateTime.parse(command.getBookingHttp().getInvoice().getInvoiceDate()),
                hotelDto,
                agencyDto,
                command.getBookingHttp().getInvoice().getAutoRec()
        );

        this.invoiceService.create(invoiceDto);
    }

    private void createBookingList(CreateInvoiceCommand command, List<ManageBookingDto> bookingDtos) {
        bookingDtos.add(new ManageBookingDto(
                command.getBookingHttp().getId(),
                command.getBookingHttp().getBookingId(),
                command.getBookingHttp().getReservationNumber(),
                LocalDateTime.parse(command.getBookingHttp().getCheckIn()),
                LocalDateTime.parse(command.getBookingHttp().getCheckOut()),
                command.getBookingHttp().getFullName(),
                command.getBookingHttp().getFirstName(),
                command.getBookingHttp().getLastName(),
                command.getBookingHttp().getInvoiceAmount(),
                command.getBookingHttp().getAmountBalance(),
                command.getBookingHttp().getCouponNumber(),
                command.getBookingHttp().getAdults(),
                command.getBookingHttp().getChildren(),
                null,
                command.getBookingHttp().getBookingParent() != null ? this.bookingService.findById(command.getBookingHttp().getBookingParent()) : null,
                LocalDateTime.parse(command.getBookingHttp().getBookingDate())
        ));
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
