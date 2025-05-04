package com.kynsoft.finamer.payment.application.services.paymentDetail.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeCommand;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckBookingExistsApplyPayment;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfNewPaymentDetailIsApplyDepositRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.PaymentTransactionTypeHttpService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Service
public class CreatePaymentDetailService {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final PaymentTransactionTypeHttpService paymentTransactionTypeHttpService;
    private final IManagePaymentStatusService statusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageEmployeeService employeeService;
    private final IManageBookingService bookingService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;

    @Getter
    private PaymentDto payment;

    @Getter
    private ManageBookingDto booking;

    public CreatePaymentDetailService(IPaymentDetailService paymentDetailService,
                                      IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                      IPaymentService paymentService,
                                      PaymentTransactionTypeHttpService paymentTransactionTypeHttpService,
                                      IManagePaymentStatusService statusService,
                                      IPaymentCloseOperationService paymentCloseOperationService,
                                      IManageEmployeeService employeeService,
                                      IManageBookingService bookingService,
                                      IPaymentStatusHistoryService paymentStatusHistoryService){
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.paymentTransactionTypeHttpService = paymentTransactionTypeHttpService;
        this.statusService = statusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.employeeService = employeeService;
        this.bookingService = bookingService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
    }

    @Transactional
    public PaymentDetailDto create(UUID id,
                                   UUID employeeId,
                                   Status status,
                                   UUID paymentId,
                                   UUID transactionTypeId,
                                   Double amount,
                                   String remark,
                                   UUID bookingId,
                                   Boolean applyPayment,
                                   IMediator mediator,
                                   UUID parentPaymentDetailId){
        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.getPaymenTransactionType(transactionTypeId, mediator);
        PaymentDetailDto parentPaymentDetail = this.getParentPaymentDetail(paymentTransactionTypeDto, parentPaymentDetailId);
        this.payment = this.getPayment(paymentTransactionTypeDto, paymentId, parentPaymentDetail);
        ManageEmployeeDto employee = this.employeeService.findById(employeeId);
        ManagePaymentStatusDto paymentStatusDto = this.statusService.findByApplied();
        this.booking = this.getBookingAndValidate(applyPayment, bookingId, amount);
        OffsetDateTime transactionDate = this.getTransactionDate(payment.getHotel().getId());

        //Hacer una regla que si el paymentTransacionType es Apply Deposit el parentPaymentDetail no debe ser null
        //RulesChecker.checkRule(new CheckIfNewPaymentDetailIsApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));

        ProcessCreatePaymentDetail createPaymentDetail = new ProcessCreatePaymentDetail(payment,
                amount,
                transactionDate,
                employee,
                remark,
                paymentTransactionTypeDto,
                paymentStatusDto,
                parentPaymentDetail
        );
        createPaymentDetail.process();
        PaymentDetailDto paymentDetail = createPaymentDetail.getDetail();

        if (applyPayment) {
            ProcessApplyPaymentDetail applyPaymentDetail = new ProcessApplyPaymentDetail(payment,
                    paymentDetail,
                    booking,
                    transactionDate,
                    amount);
            applyPaymentDetail.process();
        }

        this.saveChanges(payment, paymentDetail, applyPayment, booking, createPaymentDetail);

        return paymentDetail;
    }

    private ManagePaymentTransactionTypeDto getPaymenTransactionType(UUID code, IMediator mediator){
        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = null;
        try {
            paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(code);
        } catch (Exception e) {
            ManagePaymentTransactionTypeResponse response = paymentTransactionTypeHttpService.sendAccountStatement(new ManagePaymentTransactionTypeRequest(code));
            mediator.send(CreateManagePaymentTransactionTypeCommand.fromRequest(response));
            paymentTransactionTypeDto = response.createObject();
        }

        return paymentTransactionTypeDto;
    }

    private PaymentDetailDto getParentPaymentDetail(ManagePaymentTransactionTypeDto paymentTransactionTypeDto, UUID parentPaymentDetailId){
        if(paymentTransactionTypeDto.getApplyDeposit()){
            return this.paymentDetailService.findById(parentPaymentDetailId);
        }
        return null;
    }

    private PaymentDto getPayment(ManagePaymentTransactionTypeDto paymentTransactionType, UUID paymentId, PaymentDetailDto parentPaymentDetail){
        if(paymentTransactionType.getApplyDeposit()){
            return parentPaymentDetail.getPayment();
        }

        return this.paymentService.findByIdCustom(paymentId);
    }

    private OffsetDateTime getTransactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private ManageBookingDto getBookingAndValidate(Boolean applyPayment, UUID bookingId, Double amount){
        if(applyPayment){
            ManageBookingDto booking = this.bookingService.findById(bookingId);
            RulesChecker.checkRule(new CheckBookingExistsApplyPayment(applyPayment, booking));
            return booking;
        }

        return null;
    }

    private void saveChanges(PaymentDto payment, PaymentDetailDto paymentDetail, Boolean applyPayment, ManageBookingDto booking, ProcessCreatePaymentDetail createPaymentDetail){
        PaymentDetailDto createdDetail = this.paymentDetailService.create(paymentDetail);
        paymentDetail.setParentId(createdDetail.getPaymentDetailId());

        this.paymentService.update(payment);

        if(createPaymentDetail.isPaymentApplied()){
            PaymentStatusHistoryDto paymentStatusHistoryDto = createPaymentDetail.getPaymentStatusHistory();
            this.paymentStatusHistoryService.create(paymentStatusHistoryDto);
        }

        if(applyPayment){
            this.bookingService.update(booking);
        }
    }
}
