package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EHotelPaymentStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePaymentTransactionStatus {

    ManagePaymentTransactionStatusDto create(ManagePaymentTransactionStatusDto dto);

    void update(ManagePaymentTransactionStatusDto dto);

    void delete(UUID id);

    ManagePaymentTransactionStatusDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    Long countByCodeAndNotId(String code, UUID id);

    Long countByInProgressAndNotId(UUID id);

    Long countByCompletedAndNotId(UUID id);

    Long countByCancelledAndNotId(UUID id);

    Long countByAppliedAndNotId(UUID id);

    ManagePaymentTransactionStatusDto findByEReconcileTransactionStatus(EHotelPaymentStatus hotelPaymentStatus);

    List<ManagePaymentTransactionStatusDto> findByIds(List<UUID> ids);
}
