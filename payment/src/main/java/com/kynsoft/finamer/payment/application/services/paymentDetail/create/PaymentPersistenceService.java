package com.kynsoft.finamer.payment.application.services.paymentDetail.create;

import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessPaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PaymentPersistenceService {
    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;
    private final IManageBookingService bookingService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;

    public PaymentPersistenceService(IPaymentDetailService paymentDetailService,
                                     IPaymentService paymentService,
                                     IManageBookingService bookingService,
                                     IPaymentStatusHistoryService paymentStatusHistoryService){
        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
    }

    @Transactional
    public void saveChanges(PaymentDto payment,
                            PaymentDetailDto paymentDetail,
                            Boolean applyPayment,
                            ManageBookingDto booking,
                            ProcessPaymentDetail createPaymentDetail) {

        this.paymentDetailService.create(paymentDetail);
        this.paymentService.update(payment);

        if (createPaymentDetail.isPaymentApplied()) {
            PaymentStatusHistoryDto paymentStatusHistoryDto = createPaymentDetail.getPaymentStatusHistory();
            this.paymentStatusHistoryService.create(paymentStatusHistoryDto);
        }

        if (applyPayment) {
            this.bookingService.update(booking);
        }
    }
}
