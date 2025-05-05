package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.ImportRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IImportRoomRateService {

    UUID create(ImportRoomRateDto dto);

    List<ImportRoomRateDto> createMany(List<ImportRoomRateDto> dtoList);

    void update(ImportRoomRateDto dto);

    void updateMany(List<ImportRoomRateDto> dtoList);

    List<ImportRoomRateDto> findByImportProcessId(UUID importProcessId);

    List<ImportRoomRateDto> findByImportProcessIdAndRoomRateId(UUID importProcessId, UUID roomRateId);

    List<ImportRoomRateDto> findByImportProcessIdAndRoomRates(UUID importProcessId, List<RoomRateDto> roomRateDtoList);

    PaginatedResponse getRoomRateErrorsByImportProcessId(UUID importProcessId, Pageable pageable);
}
