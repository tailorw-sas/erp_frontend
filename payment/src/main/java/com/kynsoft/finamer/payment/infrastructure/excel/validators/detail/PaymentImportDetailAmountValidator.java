package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PaymentImportDetailAmountValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final IPaymentService service;
    private final IManagePaymentTransactionTypeService managePaymentTransactionTypeService;
    private final IManageBookingService bookingService;
    private final BookingHttpGenIdService bookingHttpGenIdService;
    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;

    protected PaymentImportDetailAmountValidator(ApplicationEventPublisher applicationEventPublisher,
            IPaymentService service,
            IManagePaymentTransactionTypeService managePaymentTransactionTypeService,
            IManageBookingService bookingService,
            BookingHttpGenIdService bookingHttpGenIdService,
            BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl) {
        super(applicationEventPublisher);
        this.service = service;
        this.managePaymentTransactionTypeService = managePaymentTransactionTypeService;
        this.bookingService = bookingService;
        this.bookingHttpGenIdService = bookingHttpGenIdService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBalance())) {
            errorFieldList.add(new ErrorField("Balance", "Payment Amount can't be empty"));
            return false;
        }
        boolean valid = obj.getBalance() > 0;
        if (!valid) {
            errorFieldList.add(new ErrorField("Balance", "Payment Amount must be greater than 0"));
            return false;
        }
        try {
            PaymentDto paymentDto = this.service.findByPaymentId(Long.parseLong(obj.getPaymentId()));
            if (Objects.isNull(obj.getBalance())) {
                errorFieldList.add(new ErrorField("Balance", "Payment Amount can't be empty"));
                return false;
            }
            try {
                ManagePaymentTransactionTypeDto transactionTypeDto = managePaymentTransactionTypeService.findByCode(obj.getTransactionType().trim());
                if (transactionTypeDto.getDeposit()) {
                    errorFieldList.add(new ErrorField("transactionType", "The transaction type is deposit."));
                    return false;
                }
                if (transactionTypeDto.getCash() && (paymentDto.getPaymentBalance() < obj.getBalance())) {//Si es de tipo cash, el balance debe ser menor o igual que el payment balance.
                    errorFieldList.add(new ErrorField("Balance", "The balance is greater than the amount balance of the payment."));
                    //errorFieldList.add(new ErrorField("Balance", "El valor del payment es menor que el balance."));
                    return false;
                }
                try {
                    ManageBookingDto bookingDto = this.getBookingDto(Long.valueOf(obj.getBookId()));
                    //Si es de tipo other deduction, el booking balance debe ser mayor o igual que el balance del excel.
                    //TODO: duda, creo que para el other deduction debia ser igual estricto. el balnce del booking y el del excel????
                    if (!transactionTypeDto.getCash() && !transactionTypeDto.getDeposit() && !transactionTypeDto.getApplyDeposit() && (bookingDto.getAmountBalance() < obj.getBalance())) {
                        errorFieldList.add(new ErrorField("bookingId", "The amount to apply is greater than the balance of the booking."));
                        return false;
                    }
                } catch (Exception e) {
                    errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                    return false;
                }

            } catch (Exception e) {
                errorFieldList.add(new ErrorField("Transaction type", "Transaction type not found."));
                return false;
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("Payment Id", "The Payment not found."));
            return false;
        }
        return true;
    }

    private ManageBookingDto getBookingDto(Long bookingId) {
        try {
            return this.bookingService.findByGenId(bookingId);
        } catch (Exception e) {
            try {
                BookingHttp bookingHttp = this.bookingHttpGenIdService.sendGetBookingHttpRequest(bookingId);
                this.bookingImportAutomaticeHelperServiceImpl.createInvoice(bookingHttp);
                return this.bookingService.findByGenId(bookingId);
            } catch (Exception ex) {
                //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
                int maxAttempts = 3;
                while (maxAttempts > 0) {
                    try {
                        return this.bookingService.findByGenId(bookingId);
                    } catch (Exception exc) {
                    }
                    maxAttempts--;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException excp) {
                    }
                }
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND, new ErrorField("booking Id", DomainErrorMessage.BOOKING_NOT_FOUND.getReasonPhrase())));
            }
        }
    }

}
