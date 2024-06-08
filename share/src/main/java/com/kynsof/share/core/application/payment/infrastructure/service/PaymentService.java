package com.kynsof.share.core.application.payment.infrastructure.service;

import com.kynsof.share.core.application.payment.domain.placeToPlay.request.ReceiptPaymentType;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.Transactions;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsState;
import com.kynsof.share.core.application.payment.domain.service.ICheckPaymentService;
import com.kynsof.share.core.application.payment.domain.service.IPaymentServiceClient;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements ICheckPaymentService {
    private final IPaymentServiceClient client;
    
    public PaymentService(IPaymentServiceClient client) {
        this.client = client;
    }

    @Override
    public boolean checkPayment(String paymentCode, String nui, ReceiptPaymentType paymentType) {
        return switch (paymentType) {
            case CORRESPONDENT ->
                    client.checkPlaceToPayPayment(paymentCode);
            case PLACETOPAY -> client.checkPlaceToPayPayment(paymentCode);
        };
    }
    
    @Override
    public TransactionsState getTransactionsState(Integer sessionId) {
        return this.client.getTransactionsState(sessionId);
    }

    @Override
    public Transactions checkPlaceToPayPaymentT(String paymentCode) {
        return this.client.checkPlaceToPayPaymentT(paymentCode);
    }

}
