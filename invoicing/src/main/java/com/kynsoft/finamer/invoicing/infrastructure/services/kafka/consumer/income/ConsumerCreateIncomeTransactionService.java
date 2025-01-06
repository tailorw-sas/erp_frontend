package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.income;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionFailedKafka;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionKafka;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionSuccessKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.NewIncomeAdjustmentRequest;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.income.ProducerCreateIncomeTransactionFailed;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.income.ProducerCreateIncomeTransactionSuccess;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUploadAttachmentUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConsumerCreateIncomeTransactionService {

    private final IMediator mediator;

    private final IManageInvoiceService manageInvoiceService;
    private final IManageBookingService manageBookingService;
    private final ProducerCreateIncomeTransactionSuccess producerCreateIncomeTransactionSuccess;
    private final ProducerCreateIncomeTransactionFailed producerCreateIncomeTransactionFailed;

    private final IPaymentService paymentService;
    private final IPaymentDetailService detailService;
    private final InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;

    public ConsumerCreateIncomeTransactionService(IMediator mediator, IManageInvoiceService manageInvoiceService,
                                                  ProducerCreateIncomeTransactionSuccess producerCreateIncomeTransactionSuccess,
                                                  ProducerCreateIncomeTransactionFailed producerCreateIncomeTransactionFailed,
                                                  IPaymentService paymentService,
                                                  IPaymentDetailService detailService,
                                                  IManageBookingService manageBookingService, InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil, IManageAttachmentTypeService attachmentTypeService, IManageResourceTypeService resourceTypeService) {

        this.mediator = mediator;
        this.manageInvoiceService = manageInvoiceService;
        this.producerCreateIncomeTransactionSuccess = producerCreateIncomeTransactionSuccess;
        this.producerCreateIncomeTransactionFailed = producerCreateIncomeTransactionFailed;
        this.paymentService = paymentService;
        this.detailService = detailService;
        this.manageBookingService = manageBookingService;
        this.invoiceUploadAttachmentUtil = invoiceUploadAttachmentUtil;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
    }

    @KafkaListener(topics = "finamer-create-income-transaction", groupId = "invoicing-entity-replica")
    public void listen(CreateIncomeTransactionKafka objKafka) {
        try {
            mediator.send(createIncomeCommand(objKafka));
            mediator.send(createIncomeAdjustmentCommand(objKafka));

//            ManageInvoiceDto invoiceDto = this.manageInvoiceService.findById(objKafka.getId());
//            ManageBookingDto bookingDto = invoiceDto.getBookings().get(0);
//
//            this.applyPayment(invoiceDto, bookingDto);

//            this.createPaymentAndDetail(objKafka.getPaymentKafka(), bookingDto);

            producerCreateIncomeTransactionSuccess.create(this.createIncomeTransactionSuccessKafka(objKafka.getId(), objKafka.getEmployeeId(), objKafka.getRelatedPaymentDetail()));
        } catch (Exception e) {
            e.printStackTrace();
            CreateIncomeTransactionFailedKafka createIncomeTransactionFailedKafka = new CreateIncomeTransactionFailedKafka(objKafka.getRelatedPaymentDetail());
            producerCreateIncomeTransactionFailed.create(createIncomeTransactionFailedKafka);
        }
    }

    private void applyPayment(ManageInvoiceDto invoiceDto, ManageBookingDto bookingDto) {
        bookingDto.setDueAmount(0.0);
        this.manageBookingService.update(bookingDto);

        invoiceDto.setDueAmount(0.0);
        this.manageInvoiceService.update(invoiceDto);
    }

    private void createPaymentAndDetail(ReplicatePaymentKafka objKafka, ManageBookingDto bookingDto) {
        
        PaymentDto payment = new PaymentDto(objKafka.getId(), objKafka.getPaymentId());
        this.paymentService.create(payment);
        this.detailService.create(new PaymentDetailDto(objKafka.getDetails().getId(), objKafka.getDetails().getPaymentDetailId(), payment, bookingDto));
    }

    private CreateIncomeCommand createIncomeCommand(CreateIncomeTransactionKafka objKafka) {
        return new CreateIncomeCommand(objKafka.getId(),
                Status.valueOf(objKafka.getStatus()),
                objKafka.getInvoiceDate(),
                objKafka.getManual(),
                objKafka.getAgency(),
                objKafka.getHotel(),
                objKafka.getInvoiceType(),
                objKafka.getIncomeAmount(),
                objKafka.getInvoiceNumber(),
                objKafka.getDueDate(),
                objKafka.getReSend(),
                objKafka.getReSendDate(),
                objKafka.getInvoiceStatus(),
                objKafka.getEmployeeId().toString(), objKafka.getAttachment() != null ? attachments(objKafka.getAttachment()) : null);
    }

    private CreateIncomeAdjustmentCommand createIncomeAdjustmentCommand(CreateIncomeTransactionKafka objKafka) {
        return new CreateIncomeAdjustmentCommand(Status.valueOf(objKafka.getStatusAdjustment()),
                objKafka.getId(),
                objKafka.getEmployeeId().toString(),
                List.of(createAdjustmentRequest(objKafka)));
    }

    private NewIncomeAdjustmentRequest createAdjustmentRequest(CreateIncomeTransactionKafka objectkafka) {
        NewIncomeAdjustmentRequest newIncomeAdjustmentRequest = new NewIncomeAdjustmentRequest();
        newIncomeAdjustmentRequest.setTransactionType(objectkafka.getTransactionTypeAdjustment());
        newIncomeAdjustmentRequest.setDate(objectkafka.getDateAdjustment());
        newIncomeAdjustmentRequest.setRemark(objectkafka.getRemark());
        newIncomeAdjustmentRequest.setAmount(objectkafka.getIncomeAmount());
        return newIncomeAdjustmentRequest;
    }

    private CreateIncomeTransactionSuccessKafka createIncomeTransactionSuccessKafka(UUID incomeId, UUID employeeId, UUID relatedPaymentDetail) {
        ManageInvoiceDto manageInvoiceDto = manageInvoiceService.findById(incomeId);
        return new CreateIncomeTransactionSuccessKafka(manageInvoiceDto.getId(),
                 manageInvoiceDto.getHotel().getId(),
                manageInvoiceDto.getAgency().getClient().getId(),
                Objects.nonNull(manageInvoiceDto.getParent()) ? manageInvoiceDto.getParent().getId() : null,
                manageInvoiceDto.getAgency().getId(),
                manageInvoiceDto.getInvoiceId(),
                manageInvoiceDto.getInvoiceNo(),
                manageInvoiceDto.getInvoiceNumber(),
                manageInvoiceDto.getInvoiceType().name(),
                manageInvoiceDto.getInvoiceAmount(),
                createManageBookingKafka(manageInvoiceDto.getBookings()),
                manageInvoiceDto.getInvoiceDate(),
                relatedPaymentDetail, employeeId
        );

    }

    private List<ManageBookingKafka> createManageBookingKafka(List<ManageBookingDto> bookings) {
        return bookings.stream().map(booking -> new ManageBookingKafka(
                booking.getId(),
                booking.getBookingId(),
                booking.getHotelBookingNumber(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getFullName(),
                booking.getFirstName(),
                booking.getLastName(),
                booking.getInvoiceAmount(),
                booking.getDueAmount(),
                booking.getCouponNumber(),
                booking.getAdults(),
                booking.getChildren(),
                Objects.nonNull(booking.getInvoice()) ? booking.getInvoice().getId() : null,
                Objects.nonNull(booking.getParent()) ? booking.getParent().getId() : null,
                booking.getBookingDate()
        )).collect(Collectors.toList());
    }

    private List<CreateIncomeAttachmentRequest> attachments(byte[] attachment){
        String filename = "detail.pdf";
        String file = "";
        try {
            LinkedHashMap<String, String> response = invoiceUploadAttachmentUtil.uploadAttachmentContent(filename, attachment);
            file = response.get("url");
        } catch (Exception e) {
            return null;
        }
        ManageAttachmentTypeDto attachmentTypeDto = this.attachmentTypeService.findDefault().orElse(null);
        ResourceTypeDto resourceTypeDto = this.resourceTypeService.findByDefaults();

        List<CreateIncomeAttachmentRequest> attachments = new ArrayList<>();
        attachments.add(new CreateIncomeAttachmentRequest(
                filename,
                file,
                "From payment.",
                attachmentTypeDto != null ? attachmentTypeDto.getId() : null,
                null,
                null,
                resourceTypeDto != null ? resourceTypeDto.getId() : null
        ));
        return attachments;
    }
}
