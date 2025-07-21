package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IManageRoomTypeService {
    UUID create(ManageRoomTypeDto dto);

    List<ManageRoomTypeDto> createMany(List<ManageRoomTypeDto> roomTypeDtos);

    void update(ManageRoomTypeDto dto);

    void delete(ManageRoomTypeDto dto);

    ManageRoomTypeDto findById(UUID id);

    ManageRoomTypeDto findByCode(String code);

    ManageRoomTypeDto findByCodeAndHotel(String code, UUID hotel);

    List<ManageRoomTypeDto> findAll();

    Map<String, UUID> findIdsByCodes(List<String> codes, UUID hotelCode);

    List<ManageRoomTypeDto> findAllByCodesAndHotel(List<String> codes, UUID hotelId);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
