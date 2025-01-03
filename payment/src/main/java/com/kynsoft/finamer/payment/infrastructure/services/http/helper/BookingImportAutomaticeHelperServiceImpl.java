package com.kynsoft.finamer.payment.infrastructure.services.http.helper;

import com.kynsof.share.core.domain.http.entity.BookingHttp;
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
import org.springframework.stereotype.Service;

@Service
public class BookingImportAutomaticeHelperServiceImpl {

    private final IManageInvoiceService invoiceService;
    private final IManageBookingService bookingService;
    private final IManageHotelService hotelService;
    private final IManageAgencyService manageAgencyService;

    public BookingImportAutomaticeHelperServiceImpl(IManageInvoiceService invoiceService,
            IManageBookingService bookingService,
            IManageHotelService hotelService,
            IManageAgencyService manageAgencyService) {
        this.invoiceService = invoiceService;
        this.bookingService = bookingService;
        this.hotelService = hotelService;
        this.manageAgencyService = manageAgencyService;
    }

    public void createInvoice(BookingHttp bookingHttp) {
        ManageHotelDto hotelDto = this.hotelService.findById(bookingHttp.getInvoice().getHotel());
        ManageAgencyDto agencyDto = this.manageAgencyService.findById(bookingHttp.getInvoice().getAgency());
        List<ManageBookingDto> bookingDtos = new ArrayList<>();
        createBookingList(bookingHttp, bookingDtos);
        create(bookingHttp, bookingDtos, hotelDto, agencyDto);
    }

    private void create(BookingHttp bookingHttp, List<ManageBookingDto> bookingDtos, ManageHotelDto hotelDto, ManageAgencyDto agencyDto) {

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                bookingHttp.getInvoice().getId(),
                bookingHttp.getInvoice().getInvoiceId(),
                bookingHttp.getInvoice().getInvoiceNo(),
                deleteHotelInfo(bookingHttp.getInvoice().getInvoiceNumber()),
                EInvoiceType.valueOf(bookingHttp.getInvoice().getInvoiceType()),
                bookingHttp.getInvoice().getInvoiceAmount(),
                bookingDtos,
                bookingHttp.getInvoice().getHasAttachment(), //!= null ? objKafka.getHasAttachment() : false
                bookingHttp.getInvoice().getInvoiceParent() != null ? this.invoiceService.findById(bookingHttp.getInvoice().getInvoiceParent()) : null,
                LocalDateTime.parse(bookingHttp.getInvoice().getInvoiceDate()),
                hotelDto,
                agencyDto,
                bookingHttp.getInvoice().getAutoRec()
        );

        this.invoiceService.create(invoiceDto);

    }

    private void createBookingList(BookingHttp bookingHttp, List<ManageBookingDto> bookingDtos) {
        bookingDtos.add(new ManageBookingDto(
                bookingHttp.getId(),
                bookingHttp.getBookingId(),
                bookingHttp.getReservationNumber(),
                LocalDateTime.parse(bookingHttp.getCheckIn()),
                LocalDateTime.parse(bookingHttp.getCheckOut()),
                bookingHttp.getFullName(),
                bookingHttp.getFirstName(),
                bookingHttp.getLastName(),
                bookingHttp.getInvoiceAmount(),
                bookingHttp.getAmountBalance(),
                bookingHttp.getCouponNumber(),
                bookingHttp.getAdults(),
                bookingHttp.getChildren(),
                null,
                bookingHttp.getBookingParent() != null ? this.bookingService.findById(bookingHttp.getBookingParent()) : null,
                LocalDateTime.parse(bookingHttp.getBookingDate())
        ));
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
