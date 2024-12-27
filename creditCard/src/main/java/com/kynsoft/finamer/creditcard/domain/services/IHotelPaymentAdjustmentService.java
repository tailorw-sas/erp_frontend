package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentAdjustmentRequest;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IHotelPaymentAdjustmentService {

    List<Long> createAdjustments(List<CreateHotelPaymentAdjustmentRequest> adjustmentRequest, Set<TransactionDto> transactionList, UUID emlpoyeeId);
}
