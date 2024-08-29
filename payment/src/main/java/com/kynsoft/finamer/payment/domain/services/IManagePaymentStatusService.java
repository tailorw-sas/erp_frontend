package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import java.util.List;

import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface IManagePaymentStatusService {

    UUID create(ManagePaymentStatusDto dto);

    void update(ManagePaymentStatusDto dto);

    void delete(ManagePaymentStatusDto dto);

    ManagePaymentStatusDto findById(UUID uuid);

    ManagePaymentStatusDto findByCode(String code);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    ManagePaymentStatusDto findByApplied();
}
