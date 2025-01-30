package com.kynsoft.finamer.payment.application.command.paymentImport.detail.applyDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeFromPaymentMessage;
import com.kynsof.share.core.domain.http.entity.income.ajustment.CreateIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.http.entity.income.ajustment.NewIncomeAdjustmentRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.ServiceLocator;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateAdjustmentHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateIncomeHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.createIncomeTransaction.ProducerCreateIncomeTransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class CreatePaymentDetailApplyDepositFromFileCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositFromFileCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService manageEmployeeService;
    private final IManageInvoiceStatusService statusService;
    private final ProducerCreateIncomeTransactionService producerCreateIncomeService;

    private final CreateIncomeHttpService createIncomeHttpService;
    private final CreateAdjustmentHttpService createAdjustmentHttpService;

    private final IManageInvoiceService manageInvoiceService;

    @Value("${payment.relate.invoice.status.code}")
    private String RELATE_INCOME_STATUS_CODE;

    private final ServiceLocator<IMediator> serviceLocator;

    public CreatePaymentDetailApplyDepositFromFileCommandHandler(IPaymentDetailService paymentDetailService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IPaymentService paymentService,
            IManageEmployeeService manageEmployeeService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService, IManageInvoiceStatusService statusService,
            ProducerCreateIncomeTransactionService producerCreateIncomeService,
            CreateIncomeHttpService createIncomeHttpService,
            CreateAdjustmentHttpService createAdjustmentHttpService,
            IManageInvoiceService manageInvoiceService,
            ServiceLocator<IMediator> serviceLocator) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.statusService = statusService;
        this.producerCreateIncomeService = producerCreateIncomeService;
        this.createIncomeHttpService = createIncomeHttpService;
        this.createAdjustmentHttpService = createAdjustmentHttpService;
        this.manageInvoiceService = manageInvoiceService;
        this.serviceLocator = serviceLocator;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositFromFileCommand command) {

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto paymentUpdate = paymentDetailDto.getPayment();

        ConsumerUpdate updatePayment = new ConsumerUpdate();

        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setApplied, paymentUpdate.getApplied() + command.getAmount(), updatePayment::setUpdate);
        //UpdateIfNotNull.updateDouble(paymentUpdate::setNotApplied, paymentUpdate.getNotApplied() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        //TODO: Se debe de validar esta variable para que cumpla con el Close Operation
        OffsetDateTime transactionDate = OffsetDateTime.now(ZoneId.of("UTC"));
        PaymentDetailDto children = new PaymentDetailDto();
        children.setId(command.getId());
        children.setStatus(command.getStatus());
        children.setPayment(paymentUpdate);
        children.setTransactionType(paymentTransactionTypeDto);
        children.setAmount(command.getAmount());
        children.setRemark(command.getRemark());
        children.setTransactionDate(transactionDate);

        children.setParentId(paymentDetailDto.getPaymentDetailId());
        this.paymentDetailService.create(children);

        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(paymentDetailDto.getChildren());
        updateChildrens.add(children);
        paymentDetailDto.setChildren(updateChildrens);
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - command.getAmount());
        paymentDetailService.update(paymentDetailDto);

        this.paymentService.update(paymentUpdate);
        command.setPaymentResponse(paymentUpdate);
        ManageInvoiceStatusDto manageInvoiceStatusDto = statusService.findByCode(RELATE_INCOME_STATUS_CODE);
        System.err.println("##################################################");
        System.err.println("Llega a la peticion http.");
        System.err.println("##################################################");
        System.err.println("##################################################");
        CreateIncomeFromPaymentMessage msg = this.createIncomeHttpService.sendCreateIncomeRequest(sendToCreateRelatedIncome(children, employeeDto, command.getTransactionTypeForAdjustment(), manageInvoiceStatusDto.getId(), command.getAttachment()));
        System.err.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.err.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.err.println("Obtener: " + msg);
        System.err.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
        System.err.println("||||||||||||||||||||||||||||||||||||||||||||||||||");
        String response = this.createAdjustmentHttpService.sendCreateIncomeRequest(this.createAdjustmentRequest(children, employeeDto.getId(), command.getTransactionTypeForAdjustment(), msg.getId()));
        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.err.println("Crendo Ajuste: " + response);
        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
        }
        ManageInvoiceDto invoice = manageInvoiceService.findById(msg.getId());
        ApplyPaymentDetailCommand applyPaymentDetailCommand = new ApplyPaymentDetailCommand(children.getId(),
                invoice.getBookings().get(0).getId(),employeeDto.getId());
        serviceLocator.getBean(IMediator.class).send(applyPaymentDetailCommand);
        //this.sendToCreateRelatedIncome(children,employeeDto.getId(),employeeDto.getFirstName(), command.getTransactionTypeForAdjustment(),manageInvoiceStatusDto.getId(), command.getAttachment());
    }

    private CreateIncomeAdjustmentRequest createAdjustmentRequest(PaymentDetailDto paymentDetailDto, UUID employeeId, UUID transactionType, UUID income) {
        CreateIncomeAdjustmentRequest request = new CreateIncomeAdjustmentRequest();
        request.setEmployee(employeeId.toString());
        request.setIncome(income);
        request.setStatus("ACTIVE");

        NewIncomeAdjustmentRequest newIncomeAdjustmentRequest = new NewIncomeAdjustmentRequest();
        newIncomeAdjustmentRequest.setTransactionType(transactionType);
        newIncomeAdjustmentRequest.setDate(LocalDate.now().toString());
        newIncomeAdjustmentRequest.setRemark(paymentDetailDto.getRemark());
        newIncomeAdjustmentRequest.setAmount(paymentDetailDto.getAmount());

        request.setAdjustments(List.of(newIncomeAdjustmentRequest));

        return request;
    }

    private CreateAntiToIncomeRequest sendToCreateRelatedIncome(PaymentDetailDto paymentDetailDto, ManageEmployeeDto employeeDto, UUID transactionType, UUID status, String attachment) {
        CreateAntiToIncomeRequest income = new CreateAntiToIncomeRequest();
        income.setInvoiceDate(LocalDateTime.now().toString());
        income.setManual(Boolean.FALSE);
        income.setAgency(paymentDetailDto.getPayment().getAgency().getId());
        income.setHotel(paymentDetailDto.getPayment().getHotel().getId());
        income.setInvoiceType(UUID.randomUUID());
        income.setInvoiceStatus(status);
        income.setIncomeAmount(paymentDetailDto.getAmount());
        income.setStatus("ACTIVE");
        income.setInvoiceNumber(0L);
        income.setDueDate(LocalDate.now().toString());
        income.setReSend(Boolean.FALSE);
        income.setReSendDate(LocalDate.now().toString());
        income.setEmployee(employeeDto.getId().toString());
        income.setAttachments(List.of(this.attachment(attachment, employeeDto)));
        return income;
    }

    private CreateAntiToIncomeAttachmentRequest attachment(String attachment, ManageEmployeeDto employeeDto) {
        return new CreateAntiToIncomeAttachmentRequest(attachment, "" + employeeDto.getFirstName() + " " + employeeDto.getLastName(), employeeDto.getId());
    }

//
//    private void sendToCreateRelatedIncome(PaymentDetailDto paymentDetailDto,UUID employeeId, String employeeName, UUID transactionType,UUID status, String attachment) {
//        PaymentDto paymentDto = paymentDetailDto.getPayment();
//        CreateIncomeTransactionKafka createIncomeTransactionSuccessKafka = new CreateIncomeTransactionKafka();
//        UUID incomeId = UUID.randomUUID();
//        createIncomeTransactionSuccessKafka.setId(incomeId);
//        createIncomeTransactionSuccessKafka.setAgency(paymentDto.getAgency().getId());
//        createIncomeTransactionSuccessKafka.setHotel(paymentDto.getHotel().getId());
//        createIncomeTransactionSuccessKafka.setInvoiceDate(LocalDateTime.now());
//        createIncomeTransactionSuccessKafka.setIncomeAmount(paymentDetailDto.getAmount());
//        createIncomeTransactionSuccessKafka.setManual(false);
//        createIncomeTransactionSuccessKafka.setInvoiceStatus(status);
//        createIncomeTransactionSuccessKafka.setStatus(Status.ACTIVE.name());
//        createIncomeTransactionSuccessKafka.setTransactionTypeAdjustment(transactionType);
//        createIncomeTransactionSuccessKafka.setEmployeeAdjustment(employeeName);
//        createIncomeTransactionSuccessKafka.setDateAdjustment(LocalDate.now());
//        createIncomeTransactionSuccessKafka.setRelatedPaymentDetail(paymentDetailDto.getId());
//        createIncomeTransactionSuccessKafka.setStatusAdjustment(Status.ACTIVE.name());
//        createIncomeTransactionSuccessKafka.setEmployeeId(employeeId);
//        createIncomeTransactionSuccessKafka.setPaymentKafka(new ReplicatePaymentKafka(paymentDto.getId(), paymentDto.getPaymentId(), new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getParentId())));
//        createIncomeTransactionSuccessKafka.setAttachment(attachment);
//        producerCreateIncomeService.create(createIncomeTransactionSuccessKafka);
//    }
}
