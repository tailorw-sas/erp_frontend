package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail.PaymentImportDetailErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentImportAmountValidator;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentTransactionIdValidator;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentDetailValidatorFactory extends IValidatorFactory<PaymentDetailRow> {

    private PaymentImportDetailAmountValidator paymentImportDetailAmountValidator;
    private PaymentDetailsNoApplyDepositValidator paymentDetailsNoApplyDepositValidator;

    private PaymentDetailExistPaymentValidator paymentDetailExistPaymentValidator;
    private PaymentDetailsBookingFieldValidator paymentDetailsBookingFieldValidator;

    private PaymentDetailBelongToSamePayment paymentDetailBelongToSamePayment;

    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentTransactionIdValidator paymentTransactionIdValidator;

    private final IPaymentService paymentService;

    private final IPaymentDetailService paymentDetailService;

    private final PaymentImportCacheRepository paymentImportCacheRepository;

    private final IManagePaymentTransactionTypeService managePaymentTransactionTypeService;
    private final IManageBookingService bookingService;

    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;
    private final BookingHttpGenIdService bookingHttpGenIdService;

    public PaymentDetailValidatorFactory(ApplicationEventPublisher paymentEventPublisher, IPaymentService paymentService,
                                         IPaymentDetailService paymentDetailService,
                                         PaymentImportCacheRepository paymentImportCacheRepository,
                                         IManagePaymentTransactionTypeService managePaymentTransactionTypeService,
                                         IManageBookingService bookingService,
                                         BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl,
                                         BookingHttpGenIdService bookingHttpGenIdService
    ) {
        super(paymentEventPublisher);
        this.paymentService = paymentService;
        this.paymentDetailService = paymentDetailService;
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.managePaymentTransactionTypeService = managePaymentTransactionTypeService;
        this.bookingService = bookingService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
        this.bookingHttpGenIdService = bookingHttpGenIdService;
    }

    @Override
    public void createValidators() {
        paymentDetailExistPaymentValidator=new PaymentDetailExistPaymentValidator(applicationEventPublisher,paymentService);
        paymentImportDetailAmountValidator = new PaymentImportDetailAmountValidator(applicationEventPublisher, paymentService, managePaymentTransactionTypeService, bookingService, bookingHttpGenIdService, bookingImportAutomaticeHelperServiceImpl);
        paymentDetailsNoApplyDepositValidator= new PaymentDetailsNoApplyDepositValidator(applicationEventPublisher,managePaymentTransactionTypeService);
        paymentDetailBelongToSamePayment = new PaymentDetailBelongToSamePayment(applicationEventPublisher,paymentService);
        paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher, paymentDetailService, paymentImportCacheRepository);
        paymentTransactionIdValidator = new PaymentTransactionIdValidator(paymentDetailService, applicationEventPublisher);
        paymentDetailsBookingFieldValidator = new PaymentDetailsBookingFieldValidator(applicationEventPublisher, bookingService, bookingImportAutomaticeHelperServiceImpl, bookingHttpGenIdService);

    }

    @Override
    public boolean validate(PaymentDetailRow toValidate) {
        int errors = 0;
        errors += paymentDetailExistPaymentValidator.validate(toValidate,errorFieldList) ? 0 : 1;
        errors += paymentDetailsBookingFieldValidator.validate(toValidate, errorFieldList) ? 0 : 1;
        errors += this.validatePaymentAmount(toValidate) ? 0 : 1;
        errors += paymentDetailsNoApplyDepositValidator.validate(toValidate,errorFieldList) ? 0 : 1;
        errors += this.validateAsAntiToIncome(toValidate) ? 0 : 1;
        errors += this.validateAsExternalPaymentId(toValidate) ? 0 : 1;
        PaymentProjection paymentDto = this.paymentService.findByPaymentIdProjection(Long.parseLong(toValidate.getPaymentId()));
        errors += this.validateAgency(toValidate, paymentDto) ? 0 : 1;
        errors += this.validateHotel(toValidate, paymentDto) ? 0 : 1;

        if (this.hasErrors()) {
            PaymentImportDetailErrorEvent paymentImportErrorEvent =
                    new PaymentImportDetailErrorEvent(new PaymentDetailRowError(null,toValidate.getRowNumber(),
                            toValidate.getImportProcessId(), errorFieldList, toValidate));
            this.sendErrorEvent(paymentImportErrorEvent);
        }
        boolean result = errors == 0;
        this.clearErrors();
        return result;

    }

    private boolean validateAgency(PaymentDetailRow toValidate, PaymentProjection paymentDto) {
        if (!toValidate.getAgencys().contains(paymentDto.getAgencyId())) {
            this.errorFieldList.add(new ErrorField("Agency", "The employee does not have access to the agency."));
            return false;
        }
        return true;
    }

    private boolean validateHotel(PaymentDetailRow toValidate, PaymentProjection paymentDto) {
        if (!toValidate.getHotels().contains(paymentDto.getHotelId())) {
            this.errorFieldList.add(new ErrorField("Hotel", "The employee does not have access to the hotel."));
            return false;
        }
        return true;
    }

    private boolean validatePaymentAmount(PaymentDetailRow toValidate){
        if (Objects.isNull(toValidate.getAnti())){
            return paymentImportDetailAmountValidator.validate(toValidate,errorFieldList);
        } return true;
    }

    private boolean validateAsAntiToIncome(PaymentDetailRow toValidate){
        int errors = 0;
        if (Objects.nonNull(toValidate.getAnti()) && toValidate.getAnti()>0){
            AntiToIncomeRow antiToIncomeRow = new AntiToIncomeRow();
            antiToIncomeRow.setTransactionId(toValidate.getAnti());
            antiToIncomeRow.setAmount(toValidate.getBalance());
            antiToIncomeRow.setRemarks(toValidate.getRemarks());
            antiToIncomeRow.setImportProcessId(toValidate.getImportProcessId());
            errors += paymentTransactionIdValidator.validate(antiToIncomeRow, errorFieldList) ? 0 : 1;
            errors += paymentImportAmountValidator.validate(antiToIncomeRow, errorFieldList) ? 0 : 1;
        }
        return errors == 0;
    }

    private boolean validateAsExternalPaymentId(PaymentDetailRow toValidate){
        if (Objects.nonNull(toValidate.getExternalPaymentId())){
            return paymentDetailBelongToSamePayment.validate(toValidate,errorFieldList);
        } return true;
    }

}
