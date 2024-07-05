package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageClientService {
    UUID create(ManageClientDto dto);

    void update(ManageClientDto dto);

    void delete(ManageClientDto dto);

    ManageClientDto findById(UUID id);
}
