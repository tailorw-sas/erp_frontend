package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.manageInvoice;

import com.kynsof.share.core.domain.kafka.entity.AttachmentKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageInvoiceKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit.CreatePaymentToCreditCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageInvoiceService {

    private final IManageInvoiceService service;
    private final IManageHotelService hotelService;
    private final IMediator mediator;

    public ConsumerReplicateManageInvoiceService(IManageInvoiceService service,
            IMediator mediator,
            IManageHotelService hotelService) {
        this.mediator = mediator;
        this.service = service;
        this.hotelService = hotelService;
    }

    @KafkaListener(topics = "finamer-replicate-manage-invoice", groupId = "payment-entity-replica")
    public void listen(ManageInvoiceKafka objKafka) {
        try {
            List<ManageBookingDto> bookingDtos = new ArrayList<>();
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
                            null
                    ));
                }
            }

            ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                    objKafka.getId(),
                    objKafka.getInvoiceId(),
                    objKafka.getInvoiceNo(),
                    objKafka.getInvoiceNumber(),
                    EInvoiceType.valueOf(objKafka.getInvoiceType()),
                    objKafka.getInvoiceAmount(),
                    bookingDtos,
                    objKafka.getHasAttachment() //!= null ? objKafka.getHasAttachment() : false
            );

            this.service.create(invoiceDto);

            if (invoiceDto.getInvoiceType().equals(EInvoiceType.CREDIT)) {
                ManageHotelDto hotelDto = this.hotelService.findById(objKafka.getHotel());
                if (hotelDto.getAutoApplyCredit()) {

                    List<CreateAttachmentRequest> attachmentKafkas = new ArrayList<>();
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
                                    attDto.getRemark()
                            ));
                        }
                    }

                    this.mediator.send(new CreatePaymentToCreditCommand(objKafka.getClient(), objKafka.getAgency(), objKafka.getHotel(), invoiceDto, attachmentKafkas, mediator));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageInvoiceService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
