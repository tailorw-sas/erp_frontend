package com.kynsoft.finamer.payment.application.command.paymentImport.detail.applyDeposit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeFromPaymentMessage;
import com.kynsof.share.core.domain.http.entity.income.ajustment.CreateIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.http.entity.income.ajustment.NewIncomeAdjustmentRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.ServiceLocator;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateAdjustmentHttpService;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateIncomeHttpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.*;
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
    private final CreateIncomeHttpService createIncomeHttpService;
    private final CreateAdjustmentHttpService createAdjustmentHttpService;
    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    @Value("${payment.relate.invoice.status.code}")
    private String RELATE_INCOME_STATUS_CODE;

    private final ServiceLocator<IMediator> serviceLocator;

    public CreatePaymentDetailApplyDepositFromFileCommandHandler(IPaymentDetailService paymentDetailService,
                                                                 IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                                 IPaymentService paymentService,
                                                                 IManageEmployeeService manageEmployeeService,
                                                                 IManageInvoiceStatusService statusService,
                                                                 CreateIncomeHttpService createIncomeHttpService,
                                                                 CreateAdjustmentHttpService createAdjustmentHttpService,
                                                                 IManageInvoiceService manageInvoiceService, IPaymentCloseOperationService paymentCloseOperationService,
                                                                 ServiceLocator<IMediator> serviceLocator) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.statusService = statusService;
        this.createIncomeHttpService = createIncomeHttpService;
        this.createAdjustmentHttpService = createAdjustmentHttpService;
        this.manageInvoiceService = manageInvoiceService;
        this.paymentCloseOperationService = paymentCloseOperationService;
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
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        PaymentDetailDto paymentDetails = new PaymentDetailDto();
        paymentDetails.setId(command.getId());
        paymentDetails.setStatus(command.getStatus());
        paymentDetails.setPayment(paymentUpdate);
        paymentDetails.setTransactionType(paymentTransactionTypeDto);
        paymentDetails.setAmount(command.getAmount());
        paymentDetails.setRemark(command.getRemark());
        paymentDetails.setTransactionDate(transactionDate(paymentUpdate.getHotel().getId()));

        paymentDetails.setParentId(paymentDetailDto.getPaymentDetailId());
        this.paymentDetailService.create(paymentDetails);

        List<PaymentDetailDto> updatePaymentDetails = new ArrayList<>();
        updatePaymentDetails.addAll(paymentDetailDto.getPaymentDetails());
        updatePaymentDetails.add(paymentDetails);
        paymentDetailDto.setPaymentDetails(updatePaymentDetails);
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - command.getAmount());
        paymentDetailService.update(paymentDetailDto);

        this.paymentService.update(paymentUpdate);
        command.setPaymentResponse(paymentUpdate);
        ManageInvoiceStatusDto manageInvoiceStatusDto = statusService.findByCode(RELATE_INCOME_STATUS_CODE);
        CreateIncomeFromPaymentMessage msg = this.createIncomeHttpService.sendCreateIncomeRequest(
                sendToCreateRelatedIncome(paymentDetails, employeeDto, command.getTransactionTypeForAdjustment(),
                        command.getAttachment()));
        this.createAdjustmentHttpService.sendCreateIncomeRequest(
                this.createAdjustmentRequest(paymentDetails, employeeDto.getId(), command.getTransactionTypeForAdjustment(),
                        msg.getId()));
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (Exception e) {
        }
        ManageInvoiceDto invoice = manageInvoiceService.findById(msg.getId());
        ApplyPaymentDetailCommand applyPaymentDetailCommand = new ApplyPaymentDetailCommand(paymentDetails.getId(),
                invoice.getBookings().get(0).getId(),employeeDto.getId());
        serviceLocator.getBean(IMediator.class).send(applyPaymentDetailCommand);
    }

    private CreateIncomeAdjustmentRequest createAdjustmentRequest(PaymentDetailDto paymentDetailDto, UUID employeeId,
                                                                  UUID transactionType, UUID income) {
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

    private CreateAntiToIncomeRequest sendToCreateRelatedIncome(PaymentDetailDto paymentDetailDto,
                                                                ManageEmployeeDto employeeDto,
                                                                UUID status, String attachment) {
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

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }
}
