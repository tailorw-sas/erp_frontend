package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManagePaymentTransactionTypeDto;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManagePaymentTransactionTypeService {

    UUID create(ManagePaymentTransactionTypeDto dto);

    void update(ManagePaymentTransactionTypeDto dto);

    void delete(ManagePaymentTransactionTypeDto dto);

    ManagePaymentTransactionTypeDto findById(UUID id);

    ManagePaymentTransactionTypeDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
