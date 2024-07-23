package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageRoomRateService {

    UUID create(ManageRoomRateDto dto);

    void update(ManageRoomRateDto dto);

    void calculateInvoiceAmount(ManageRoomRateDto dto);

    void delete(ManageRoomRateDto dto);

    ManageRoomRateDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageRoomRateDto> findByIds(List<UUID> ids);
}
