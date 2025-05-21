package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageInvoice;

import com.kynsof.share.core.domain.kafka.entity.AttachmentKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageInvoiceKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit.CreatePaymentToCreditCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerReplicateManageInvoiceService {

    private final IManageInvoiceService service;
    private final IManageHotelService hotelService;
    private final IMediator mediator;
    private final IManageBookingService serviceBookingService;
    private final IManageHotelService manageHotelService;
    private final IManageAgencyService manageAgencyService;

    public ConsumerReplicateManageInvoiceService(IManageInvoiceService service,
            IMediator mediator,
            IManageHotelService hotelService,
            IManageBookingService serviceBookingService,
            IManageHotelService manageHotelService,
            IManageAgencyService manageAgencyService) {
        this.mediator = mediator;
        this.service = service;
        this.hotelService = hotelService;
        this.serviceBookingService = serviceBookingService;
        this.manageHotelService = manageHotelService;
        this.manageAgencyService = manageAgencyService;
    }

    @KafkaListener(topics = "finamer-replicate-manage-invoice", groupId = "payment-entity-replica")
    public void listen(ManageInvoiceKafka objKafka) {
        ManageHotelDto manageHotelDto = manageHotelService.findById(objKafka.getHotel());
        ManageAgencyDto manageAgencyDto = manageAgencyService.findById(objKafka.getAgency());

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                objKafka.getId(),
                objKafka.getInvoiceId(),
                objKafka.getInvoiceNo(),
                deleteHotelInfo(objKafka.getInvoiceNumber()),
                EInvoiceType.valueOf(objKafka.getInvoiceType()),
                objKafka.getInvoiceAmount(),
                null,
                objKafka.getHasAttachment(),
                objKafka.getInvoiceParent() != null ? this.service.findById(objKafka.getInvoiceParent()) : null,
                objKafka.getInvoiceDate(),
                manageHotelDto,
                manageAgencyDto,
                objKafka.getAutoRec()
        );

        this.service.create(invoiceDto);

        List<ManageBookingDto> bookingDtos = new ArrayList<>();
        this.createBookingList(objKafka, bookingDtos, invoiceDto);
        this.serviceBookingService.updateAllBooking(bookingDtos);

        invoiceDto.setBookings(bookingDtos);

        if (invoiceDto.getInvoiceType().equals(EInvoiceType.CREDIT)) {
            this.automaticProcessApplyPayment(objKafka, invoiceDto);
        }

        if (invoiceDto.getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {//check marcado
            this.automaticProcessApplyPayment(objKafka, invoiceDto);
        }
    }

    private void createBookingList(ManageInvoiceKafka objKafka, List<ManageBookingDto> bookingDtos, ManageInvoiceDto invoiceDto) {
        if (objKafka.getBookings() != null) {
            for (ManageBookingKafka booking : objKafka.getBookings()) {
                bookingDtos.add(new ManageBookingDto(
                        booking.getId(),
                        booking.getBookingId(),
                        booking.getReservationNumber(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        booking.getFullName(),
                        booking.getFirstName(),
                        booking.getLastName(),
                        booking.getInvoiceAmount(),
                        booking.getAmountBalance(),
                        booking.getCouponNumber(),
                        booking.getAdults(),
                        booking.getChildren(),
                        invoiceDto,
                        booking.getBookingParent() != null ? this.serviceBookingService.findById(booking.getBookingParent()) : null,
                        booking.getBookingDate()
                ));
            }
        }
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

    private void addAttachment(ManageInvoiceKafka objKafka, List<CreateAttachmentRequest> attachmentKafkas) {
        if (objKafka.getAttachments() != null) {
            for (AttachmentKafka attDto : objKafka.getAttachments()) {
                attachmentKafkas.add(new CreateAttachmentRequest(
                        Status.ACTIVE,
                        attDto.getEmployee(),
                        null,
                        null,
                        attDto.getFileName(),
                        "",
                        attDto.getPath(),
                        attDto.getRemark(),
                        attDto.isSupport()
                ));
            }
        }
    }

    private void automaticProcessApplyPayment(ManageInvoiceKafka objKafka, ManageInvoiceDto invoiceDto) {

        List<CreateAttachmentRequest> attachmentKafkas = new ArrayList<>();
        this.addAttachment(objKafka, attachmentKafkas);
        this.mediator.send(new CreatePaymentToCreditCommand(objKafka.getClient(), objKafka.getAgency(), objKafka.getHotel(), invoiceDto, attachmentKafkas, objKafka.getEmployee()));
    }
}
