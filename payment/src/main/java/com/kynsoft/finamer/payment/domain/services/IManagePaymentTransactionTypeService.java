package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManagePaymentTransactionTypeService {

    UUID create(ManagePaymentTransactionTypeDto dto);

    void update(ManagePaymentTransactionTypeDto dto);

    void delete(ManagePaymentTransactionTypeDto dto);

    ManagePaymentTransactionTypeDto findById(UUID id);

    ManagePaymentTransactionTypeDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ManagePaymentTransactionTypeDto findByPaymentInvoice();

    ManagePaymentTransactionTypeDto findByPaymentInvoiceCacheable();

    ManagePaymentTransactionTypeDto findByDeposit();

    ManagePaymentTransactionTypeDto findByApplyDeposit();

    void clearCache();

    ManagePaymentTransactionTypeDto findByApplyDepositAndDefaults();
}
