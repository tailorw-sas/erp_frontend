package com.kynsoft.finamer.payment.infrastructure.excel.event.error.expenseToBooking;

import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.excel.IPaymentImportExtrasFieldProcessor;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseBookingErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseErrorRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PaymentExpenseBookingExtrasFieldProcessor implements IPaymentImportExtrasFieldProcessor<PaymentExpenseBookingRowError> {
    private final IManageBookingService bookingService;
    private final PaymentImportExpenseBookingErrorRepository errorRepository;
    public PaymentExpenseBookingExtrasFieldProcessor(IManageBookingService bookingService, PaymentImportExpenseBookingErrorRepository errorRepository) {
        this.bookingService = bookingService;
        this.errorRepository = errorRepository;
    }


    @Override
    public PaymentExpenseBookingRowError addExtrasField(PaymentExpenseBookingRowError rowError) {
        String bookingId =rowError.getRow().getBookingId();
        if (Objects.nonNull(bookingId) && !bookingId.isEmpty()) {
            ManageBookingDto bookingDto = bookingService.findByGenId(Long.parseLong(bookingId));
            PaymentExpenseBookingRow expenseBookingRow = rowError.getRow();
            expenseBookingRow.setInvoiceNo(String.valueOf(bookingDto.getInvoice().getInvoiceNo()));
            expenseBookingRow.setFullName(bookingDto.getFullName());
            expenseBookingRow.setReservationNumber(bookingDto.getReservationNumber());
            expenseBookingRow.setCouponNumber(bookingDto.getCouponNumber());
            expenseBookingRow.setCheckIn(bookingDto.getCheckIn().toString());
            expenseBookingRow.setCheckOut(bookingDto.getCheckOut().toString());
            expenseBookingRow.setAdults(bookingDto.getAdults());
            expenseBookingRow.setChildren(bookingDto.getChildren());
            rowError.setRow(expenseBookingRow);
            return errorRepository.save(rowError);
        }
        return rowError;
    }
}
