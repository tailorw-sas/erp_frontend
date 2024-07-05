package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITransactionService {

    Long create(TransactionDto dto);

    void update(TransactionDto dto);

    void delete(TransactionDto dto);

    TransactionDto findById(Long id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
