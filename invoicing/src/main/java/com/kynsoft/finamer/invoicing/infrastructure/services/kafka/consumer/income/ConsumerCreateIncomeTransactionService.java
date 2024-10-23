package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.income;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionFailedKafka;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionKafka;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionSuccessKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageBookingKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.NewIncomeAdjustmentRequest;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.income.ProducerCreateIncomeTransactionFailed;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.income.ProducerCreateIncomeTransactionSuccess;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsumerCreateIncomeTransactionService {

    private final IMediator mediator;

    private final IManageInvoiceService manageInvoiceService;
    private final ProducerCreateIncomeTransactionSuccess producerCreateIncomeTransactionSuccess;
    private final ProducerCreateIncomeTransactionFailed producerCreateIncomeTransactionFailed;

    public ConsumerCreateIncomeTransactionService(IMediator mediator, IManageInvoiceService manageInvoiceService,
                                                  ProducerCreateIncomeTransactionSuccess producerCreateIncomeTransactionSuccess,
                                                  ProducerCreateIncomeTransactionFailed producerCreateIncomeTransactionFailed) {

        this.mediator = mediator;
        this.manageInvoiceService = manageInvoiceService;
        this.producerCreateIncomeTransactionSuccess = producerCreateIncomeTransactionSuccess;
        this.producerCreateIncomeTransactionFailed = producerCreateIncomeTransactionFailed;
    }

    @KafkaListener(topics = "finamer-create-income-transaction", groupId = "income-entity-replica")
    public void listen(CreateIncomeTransactionKafka objKafka) {
        try {
            mediator.send(createIncomeCommand(objKafka));
            mediator.send(createIncomeAdjustmentCommand(objKafka));
            producerCreateIncomeTransactionSuccess.create(this.createIncomeTransactionSuccessKafka(objKafka.getId(), objKafka.getRelatedPaymentDetail()));
        } catch (Exception e) {
            CreateIncomeTransactionFailedKafka createIncomeTransactionFailedKafka = new CreateIncomeTransactionFailedKafka(objKafka.getRelatedPaymentDetail());
            producerCreateIncomeTransactionFailed.create(createIncomeTransactionFailedKafka);
        }
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
                objKafka.getEmployee(), null);
    }

    private CreateIncomeAdjustmentCommand createIncomeAdjustmentCommand(CreateIncomeTransactionKafka objKafka) {
        return new CreateIncomeAdjustmentCommand(Status.valueOf(objKafka.getStatusAdjustment()),
                objKafka.getId(),
                objKafka.getEmployeeAdjustment(),
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

    private CreateIncomeTransactionSuccessKafka createIncomeTransactionSuccessKafka(UUID incomeId, UUID relatedPaymentDetail) {
        ManageInvoiceDto manageInvoiceDto = manageInvoiceService.findById(incomeId);
        return new CreateIncomeTransactionSuccessKafka(manageInvoiceDto.getId()
                , manageInvoiceDto.getHotel().getId(),
                manageInvoiceDto.getAgency().getClient().getId(),
                Objects.nonNull(manageInvoiceDto.getParent())?manageInvoiceDto.getParent().getId():null,
                manageInvoiceDto.getAgency().getId(),
                manageInvoiceDto.getInvoiceId(),
                manageInvoiceDto.getInvoiceNo(),
                manageInvoiceDto.getInvoiceNumber(),
                manageInvoiceDto.getInvoiceType().name(),
                manageInvoiceDto.getInvoiceAmount(),
                createManageBookingKafka(manageInvoiceDto.getBookings()),
                manageInvoiceDto.getInvoiceDate(),
                relatedPaymentDetail

        );

    }

    private List<ManageBookingKafka> createManageBookingKafka(List<ManageBookingDto> bookings) {
        return bookings.stream().map(booking -> new ManageBookingKafka(
                booking.getId(),
                booking.getBookingId(),
                booking.getReservationNumber().toString(),
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

}
