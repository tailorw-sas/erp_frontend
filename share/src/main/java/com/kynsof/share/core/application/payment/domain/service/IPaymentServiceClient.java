package com.kynsof.share.core.application.payment.domain.service;

import com.kynsof.share.core.application.payment.domain.placeToPlay.request.PaymentData;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.PaymentResponse;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.Transactions;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsResponse;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsState;

public interface IPaymentServiceClient {

    boolean checkPlaceToPayPayment(String paymentCode);
    
    Transactions checkPlaceToPayPaymentT(String paymentCode);

    PaymentResponse paymentTransactions(PaymentData paymentData);

    TransactionsState getTransactionsState(Integer requestId);

    String civilRegistrations();
    TransactionsResponse getAllTransactionsClient(String idClient, Integer Page, Integer PageSize);

}
