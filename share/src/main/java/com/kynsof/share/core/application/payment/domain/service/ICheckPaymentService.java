package com.kynsof.share.core.application.payment.domain.service;


import com.kynsof.share.core.application.payment.domain.placeToPlay.request.ReceiptPaymentType;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.Transactions;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsState;

public interface ICheckPaymentService {
    boolean checkPayment(String paymentCode, String nui, ReceiptPaymentType paymentType);
    TransactionsState getTransactionsState(Integer sessionId);

    Transactions checkPlaceToPayPaymentT(String paymentCode);
}
