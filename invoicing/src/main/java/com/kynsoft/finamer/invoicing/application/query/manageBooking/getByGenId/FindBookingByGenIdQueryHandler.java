package com.kynsoft.finamer.invoicing.application.query.manageBooking.getByGenId;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.http.entity.AttachmentHttp;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FindBookingByGenIdQueryHandler implements IQueryHandler<FindBookingByGenIdQuery, BookingHttp> {

    private final IManageBookingService service;

    public FindBookingByGenIdQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public BookingHttp handle(FindBookingByGenIdQuery query) {
        ManageBookingDto response = service.findBookingId(query.getId());

        return createBookingHttp(response);
    }

    private BookingHttp createBookingHttp(ManageBookingDto response) {
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

    private InvoiceHttp createInvoiceHttp(ManageInvoiceDto invoice) {
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
