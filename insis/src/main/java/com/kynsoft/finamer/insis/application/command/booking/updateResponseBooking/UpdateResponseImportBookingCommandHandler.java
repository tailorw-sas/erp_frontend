package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.dto.ImportRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import com.kynsoft.finamer.insis.domain.services.IImportRoomRateService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UpdateResponseImportBookingCommandHandler implements ICommandHandler<UpdateResponseImportBookingCommand> {
    private final IRoomRateService roomRateService;
    private final IImportProcessService importProcessService;
    private final IImportRoomRateService importRoomRateService;

    public UpdateResponseImportBookingCommandHandler(IRoomRateService roomRateService,
                                                     IImportProcessService importProcessService,
                                                     IImportRoomRateService importRoomRateService){
        this.roomRateService = roomRateService;
        this.importProcessService = importProcessService;
        this.importRoomRateService = importRoomRateService;
    }

    @Override
    public void handle(UpdateResponseImportBookingCommand command) {
        ImportProcessDto importProcess = getImportProcess(command.getImportProcessId());
        List<ImportRoomRateDto> importRoomRates = importRoomRateService.findByImportProcessId(importProcess.getId());

        if(command.getErrorResponses().isEmpty()){
            saveBookingsResponseWhenSuccessfull(importRoomRates);
        }else{
            saveBookingsResponseWhenFailed(importRoomRates, command.getErrorResponses());
        }

        int totalFailed = getTotalFailed(importRoomRates);
        updateImportProcessStatus(importProcess, ImportProcessStatus.COMPLETED, totalFailed);
    }

    private ImportProcessDto getImportProcess(UUID id){
        return importProcessService.findById(id);
    }

    private void saveBookingsResponseWhenSuccessfull(List<ImportRoomRateDto> importRoomRates){
        List<RoomRateDto> roomRates = new ArrayList<>();
        importRoomRates.forEach(importBookingDto -> {
            importBookingDto.setUpdatedAt(LocalDateTime.now());
            RoomRateDto roomRateDto = importBookingDto.getRoomRate();
            roomRateDto.setStatus(RoomRateStatus.PROCESSED);
            roomRateDto.setUpdatedAt(LocalDateTime.now());
            roomRates.add(roomRateDto);
        });

        importRoomRateService.updateMany(importRoomRates);
        updateRoomRatesStatus(roomRates);
    }

    private void saveBookingsResponseWhenFailed(List<ImportRoomRateDto> importRoomRates, List<ErrorResponse> errorResponses) {
        List<RoomRateDto> roomRates = new ArrayList<>();

        Map<UUID, String> errorMap = errorResponses.stream()
                .collect(Collectors.toMap(ErrorResponse::getBookingId, ErrorResponse::getErrorMessage));

        importRoomRates.forEach(importRoomRate -> {
            RoomRateDto roomRateDto = importRoomRate.getRoomRate();
            if (errorMap.containsKey(roomRateDto.getId())) {
                importRoomRate.setErrorMessage(errorMap.get(roomRateDto.getId()));
                roomRateDto.setStatus(RoomRateStatus.FAILED);
            }else{
                roomRateDto.setStatus(RoomRateStatus.PENDING);
            }
            importRoomRate.setUpdatedAt(LocalDateTime.now());
            roomRateDto.setUpdatedAt(LocalDateTime.now());
            roomRates.add(roomRateDto);
        });

        importRoomRateService.updateMany(importRoomRates);
        updateRoomRatesStatus(roomRates);
    }

    private void updateRoomRatesStatus(List<RoomRateDto> roomRates){
        roomRateService.updateMany(roomRates);
    }

    private void updateImportProcessStatus(ImportProcessDto importProcess, ImportProcessStatus status, int totalFailed){
        importProcess.setStatus(status);
        importProcess.setCompletedAt(LocalDateTime.now());
        importProcess.setTotalSuccessful(importProcess.getTotalBookings() - totalFailed);
        importProcess.setTotalFailed(totalFailed);
        importProcessService.update(importProcess);
    }

    private int getTotalFailed(List<ImportRoomRateDto> importRoomRates){
        return importRoomRates.stream()
                .filter(importBookingDto -> Objects.nonNull(importBookingDto.getErrorMessage()))
                .toList().size();
    }
}
