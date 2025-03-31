package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.CreatePaymentDetailsRequest;

import java.time.OffsetDateTime;
import java.util.List;

public interface IPaymentDetailHelperService {

    void processPaymentDetails(CreatePaymentDetailsRequest request);

    void applyPaymentDetail(ManageBookingDto booking,
                            PaymentDetailDto paymentDetail,
                            OffsetDateTime transactionDate,
                            PaymentDto payment,
                            ManageEmployeeDto employee,
                            ManagePaymentStatusDto paymentStatus,
                            List<PaymentStatusHistoryDto> paymentStatusHistoryList);
}
