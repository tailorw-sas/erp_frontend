package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITransactionService {

    Long create(TransactionDto dto);

    void update(TransactionDto dto);

    TransactionDto findByUuid(UUID uuid);

    void delete(TransactionDto dto);

    TransactionDto findById(Long id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    /*PaginatedResponse searchData(Pageable pageable, List<FilterCriteria> filterCriteria);*/

    Long countByReservationNumberAndManageHotelIdAndNotId(String reservationNumber, UUID hotel);

    boolean compareParentTransactionAmount(Long parentId, Double amount);

    Double findSumOfAmountByParentId(Long parentId);

    boolean confirmCreateTransaction(UUID transactionUUID);

    void confirmTransactionMail(UUID transactionUUID);
}
