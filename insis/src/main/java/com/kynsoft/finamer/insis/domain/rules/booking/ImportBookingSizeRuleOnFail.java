package com.kynsoft.finamer.insis.domain.rules.booking;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.insis.domain.dto.ImportRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IImportRoomRateService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ImportBookingSizeRuleOnFail implements RulesChecker.IBrokenRuleCallback {

    private final UUID importProcessId;
    private final List<RoomRateDto> roomRatesToImport;
    private final List<RoomRateDto> roomRatesAvailable;
    private final IImportRoomRateService importRoomRateService;

    public ImportBookingSizeRuleOnFail(UUID importProcessId,
                                       List<RoomRateDto> roomRatesToImport,
                                       List<RoomRateDto> roomRatesAvailable,
                                       IImportRoomRateService importRoomRateService){
        this.importProcessId = importProcessId;
        this.roomRatesToImport = roomRatesToImport;
        this.roomRatesAvailable = roomRatesAvailable;
        this.importRoomRateService = importRoomRateService;
    }

    @Override
    public void onFail() {
        List<RoomRateDto> duplicatedRoomRates = removeAllById(roomRatesToImport, roomRatesAvailable);
        List<ImportRoomRateDto> duplicatedImportBookings = importRoomRateService.findByImportProcessIdAndRoomRates(importProcessId, duplicatedRoomRates);

        duplicatedImportBookings.forEach(
                bookingWithError -> {
                    bookingWithError.setErrorMessage("The booking already exists in another import process");
                    bookingWithError.setUpdatedAt(LocalDateTime.now());
                }
        );
        importRoomRateService.updateMany(duplicatedImportBookings);
    }

    private List<RoomRateDto> removeAllById(List<RoomRateDto> roomRate, List<RoomRateDto> roomRateToRemove){
        Set<UUID> roomRatesToRemoveIds = roomRateToRemove.stream()
                .map(RoomRateDto::getId)
                .collect(Collectors.toSet());

        return roomRate.stream()
                .filter(roomRateDto -> !roomRatesToRemoveIds.contains(roomRateDto.getId()))
                .toList();
    }
}
