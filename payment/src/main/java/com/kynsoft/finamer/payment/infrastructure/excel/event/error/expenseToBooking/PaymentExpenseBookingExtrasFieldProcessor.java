package com.kynsoft.finamer.payment.infrastructure.excel.event.error.expenseToBooking;

import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.excel.IPaymentImportExtrasFieldProcessor;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseBookingErrorRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PaymentExpenseBookingExtrasFieldProcessor implements IPaymentImportExtrasFieldProcessor<PaymentExpenseBookingRowError> {
    private final IManageBookingService bookingService;
    private final PaymentImportExpenseBookingErrorRepository errorRepository;
    private final IManagePaymentTransactionTypeService transactionTypeService;
    public PaymentExpenseBookingExtrasFieldProcessor(IManageBookingService bookingService, PaymentImportExpenseBookingErrorRepository errorRepository, IManagePaymentTransactionTypeService transactionTypeService) {
        this.bookingService = bookingService;
        this.errorRepository = errorRepository;
        this.transactionTypeService = transactionTypeService;
    }


    @Override
    public PaymentExpenseBookingRowError addExtrasField(PaymentExpenseBookingRowError rowError) {
        String bookingId =rowError.getRow().getBookingId();
        if (Objects.nonNull(bookingId) && !bookingId.isEmpty()) {
            PaymentExpenseBookingRow expenseBookingRow = rowError.getRow();
            try {
                ManagePaymentTransactionTypeDto transactionTypeDto = transactionTypeService.findByCode(expenseBookingRow.getTransactionType());
                expenseBookingRow.setTransactionType(transactionTypeDto.getCode() + "-" + transactionTypeDto.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ManageBookingDto bookingDto = bookingService.findByGenId(Long.parseLong(bookingId));

                expenseBookingRow.setInvoiceNo(bookingDto.getInvoice().getInvoiceNumber());
                expenseBookingRow.setFullName(bookingDto.getFullName());
                expenseBookingRow.setReservationNumber(bookingDto.getReservationNumber());
                expenseBookingRow.setCouponNumber(bookingDto.getCouponNumber());
                expenseBookingRow.setCheckIn(bookingDto.getCheckIn().toString());
                expenseBookingRow.setCheckOut(bookingDto.getCheckOut().toString());
                expenseBookingRow.setAdults(bookingDto.getAdults());
                expenseBookingRow.setChildren(bookingDto.getChildren());

                rowError.setRow(expenseBookingRow);
                return errorRepository.save(rowError);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        return rowError;
    }
}
