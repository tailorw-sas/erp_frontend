package com.kynsoft.finamer.invoicing.infrastructure.services.http.helper;

import com.kynsof.share.core.domain.http.entity.AttachmentHttp;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BookingImportAutomaticeHelperServiceImpl {
    private final IManageInvoiceService invoiceService;

    public BookingImportAutomaticeHelperServiceImpl(IManageInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    public BookingHttp createBookingHttp(ManageBookingDto response) {
        BookingHttp bookingHttp = new BookingHttp();
        bookingHttp.setId(response.getId());
        bookingHttp.setBookingId(response.getBookingId());
        bookingHttp.setReservationNumber(response.getHotelBookingNumber());
        bookingHttp.setCheckIn(response.getCheckIn().toString());
        bookingHttp.setCheckOut(response.getCheckOut().toString());
        bookingHttp.setFullName(response.getFullName());
        bookingHttp.setFirstName(response.getFirstName());
        bookingHttp.setLastName(response.getLastName());
        bookingHttp.setInvoiceAmount(response.getInvoiceAmount());
        bookingHttp.setAmountBalance(response.getDueAmount());
        bookingHttp.setCouponNumber(response.getCouponNumber());
        bookingHttp.setAdults(response.getAdults());
        bookingHttp.setChildren(response.getChildren());
        bookingHttp.setBookingParent(response.getParent() != null ? response.getParent().getId() : null);
        bookingHttp.setInvoice(createInvoiceHttp(response.getInvoice()));
        bookingHttp.setBookingDate(response.getBookingDate().toString());

        return bookingHttp;
    }

    private List<BookingHttp> createBookingHttpForInvoice(List<ManageBookingDto> response) {
        if (response == null) {
            return Collections.emptyList();
        }

        List<BookingHttp> bookings = new ArrayList<>();
        for (ManageBookingDto manageBookingDto : response) {
            BookingHttp bookingHttp = new BookingHttp();
            bookingHttp.setId(manageBookingDto.getId());
            bookingHttp.setBookingId(manageBookingDto.getBookingId());
            bookingHttp.setReservationNumber(manageBookingDto.getHotelBookingNumber());
            bookingHttp.setCheckIn(manageBookingDto.getCheckIn().toString());
            bookingHttp.setCheckOut(manageBookingDto.getCheckOut().toString());
            bookingHttp.setFullName(manageBookingDto.getFullName());
            bookingHttp.setFirstName(manageBookingDto.getFirstName());
            bookingHttp.setLastName(manageBookingDto.getLastName());
            bookingHttp.setInvoiceAmount(manageBookingDto.getInvoiceAmount());
            bookingHttp.setAmountBalance(manageBookingDto.getDueAmount());
            bookingHttp.setCouponNumber(manageBookingDto.getCouponNumber());
            bookingHttp.setAdults(manageBookingDto.getAdults());
            bookingHttp.setChildren(manageBookingDto.getChildren());
            bookingHttp.setBookingParent(manageBookingDto.getParent() != null ? manageBookingDto.getParent().getId() : null);
            bookingHttp.setBookingDate(manageBookingDto.getBookingDate().toString());
            bookings.add(bookingHttp);
        }

        return bookings;
    }

    public InvoiceHttp createInvoiceHttp(ManageInvoiceDto invoice) {
        InvoiceHttp invoiceHttp = new InvoiceHttp();
        invoiceHttp.setId(invoice.getId());
        invoiceHttp.setHotel(invoice.getHotel().getId());
        invoiceHttp.setClient(invoice.getAgency().getClient().getId());
        invoiceHttp.setInvoiceParent(invoice.getParent() != null ? invoice.getParent().getId() : null);
        invoiceHttp.setAgency(invoice.getAgency().getId());
        invoiceHttp.setInvoiceId(invoice.getInvoiceId());
        invoiceHttp.setInvoiceNo(invoice.getInvoiceNo());
        invoiceHttp.setInvoiceNumber(invoice.getInvoiceNumber());
        invoiceHttp.setInvoiceType(invoice.getInvoiceType().name());
        invoiceHttp.setInvoiceAmount(invoice.getInvoiceAmount());
        invoiceHttp.setAttachments(createAttachmentHttp(invoice));
        invoiceHttp.setHasAttachment(invoice.getAttachments() != null && !invoice.getAttachments().isEmpty());
        invoiceHttp.setInvoiceDate(invoice.getInvoiceDate().toString());
        invoiceHttp.setAutoRec(invoice.getAutoRec());

        ManageInvoiceDto invoiceDto = this.invoiceService.findById(invoice.getId());
        invoiceHttp.setBookings(createBookingHttpForInvoice(invoiceDto.getBookings()));

        return invoiceHttp;
    }

    private List<AttachmentHttp> createAttachmentHttp(ManageInvoiceDto invoice) {
        if (invoice == null || invoice.getAttachments() == null) {
            return Collections.emptyList();
        }

        List<AttachmentHttp> attachments = new ArrayList<>();
        invoice.getAttachments().forEach(a -> {
            attachments.add(new AttachmentHttp(
                    a.getId(),
                    a.getEmployeeId(),
                    a.getFilename(),
                    "",
                    a.getFile(),
                    a.getRemark(),
                    false
            ));
        });

        return attachments;
    }

    //Ahi que agregar un flujo para saber si tiene attachment supor
}
