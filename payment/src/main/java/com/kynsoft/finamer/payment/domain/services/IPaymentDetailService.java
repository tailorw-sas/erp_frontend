package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.dto.projection.paymentDetails.PaymentDetailSimple;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IPaymentDetailService {
    PaymentDetailDto create(PaymentDetailDto dto);

    void update(PaymentDetailDto dto);

    void delete(PaymentDetailDto dto);

    PaymentDetailDto findById(UUID id);

    PaymentDetailDto findByIdInWrite(UUID id);

    PaymentDetailDto findByGenId(int id);

    boolean existByGenId(int id);

    List<PaymentDetailDto> findByPaymentId(UUID paymentId);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<UUID> bulk(List<PaymentDetailDto> toSave);

    PaymentDetailDto findByPaymentDetailId(Long paymentDetailId);

    Long countByApplyPaymentAndPaymentId(UUID id);

    PaymentDetailSimpleDto findSimpleDetailByGenId(int id);

    List<PaymentDetailDto> findByIdIn(List<UUID> ids);

    Long countByPaymentDetailIdAndTransactionTypeDeposit(UUID payment);

    PaymentDetailSimple findPaymentDetailsSimpleCacheableByGenId(int id);
}
