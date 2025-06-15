package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IManageRoomRateService {

    UUID create(ManageRoomRateDto dto);

    UUID insert(ManageRoomRateDto dto);

    List<ManageRoomRateDto> insertAll(List<ManageRoomRateDto> roomRateList);

    void update(ManageRoomRateDto dto);

    List<ManageRoomRateDto> findByBooking(UUID bookingId);

    void calculateInvoiceAmount(ManageRoomRateDto dto, Double adjustmentOldAmount, Double adjustmentNewAmount);

    void delete(ManageRoomRateDto dto);

    ManageRoomRateDto findById(UUID id);

    ManageRoomRateDto findById(UUID id, boolean includeAdjustments);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageRoomRateDto> findByIds(List<UUID> ids);

    void deleteInvoice(ManageRoomRateDto dto);

    void createAll(List<ManageRoomRate> roomRates);
}
