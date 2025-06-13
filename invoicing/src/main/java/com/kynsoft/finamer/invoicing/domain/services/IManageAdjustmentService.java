package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageAdjustmentService {

    UUID create(ManageAdjustmentDto dto);

    UUID insert(ManageAdjustmentDto dto);

    List<ManageAdjustmentDto> insertAll(List<ManageAdjustmentDto> dtos);

    void create(List<ManageAdjustmentDto> dtos);

    void update(ManageAdjustmentDto dto);

    void delete(ManageAdjustmentDto dto);

    ManageAdjustmentDto findById(UUID id);


    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageAdjustmentDto> findByIds(List<UUID> ids);

    void deleteByIds(List<UUID> ids);

    void deleteInvoice(ManageAdjustmentDto dto);

    List<ManageAdjustmentDto> findByRoomRateId(UUID roomRateId);

    void createAll(List<ManageAdjustment> adjustments);
}
