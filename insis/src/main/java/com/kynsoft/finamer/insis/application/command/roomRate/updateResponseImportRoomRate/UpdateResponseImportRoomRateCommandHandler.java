package com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ImportRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IImportRoomRateService;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UpdateResponseImportRoomRateCommandHandler implements ICommandHandler<UpdateResponseImportRoomRateCommand> {

    private final IRoomRateService roomRateService;
    private final IImportProcessService importProcessService;
    private final IImportRoomRateService importRoomRateService;

    public UpdateResponseImportRoomRateCommandHandler(IRoomRateService roomRateService,
                                                      IImportProcessService importProcessService,
                                                      IImportRoomRateService importRoomRateService){
        this.roomRateService = roomRateService;
        this.importProcessService = importProcessService;
        this.importRoomRateService = importRoomRateService;
    }

    @Override
    public void handle(UpdateResponseImportRoomRateCommand command) {
        ImportProcessDto importProcess = getImportProcess(command.getImportProcessId());
        if(command.getProcessed()){
            saveBookingsResponseWhenSuccessfull(importProcess.getId(), command.getResponses());
        }else{
            saveBookingsResponseWhenFailed(importProcess.getId(), command.getResponses());
        }

        int totalFailed = getTotalFailed(command.getImportProcessId());
        updateImportProcessStatus(importProcess, ImportProcessStatus.COMPLETED, totalFailed);
    }

    private ImportProcessDto getImportProcess(UUID id){
        return importProcessService.findById(id);
    }

    private void saveBookingsResponseWhenSuccessfull(UUID importProcessId, List<RoomRateResponse> roomRateResponses){
        List<ImportRoomRateDto> importRoomRates = importRoomRateService.findByImportProcessId(importProcessId);

        Map<UUID, UUID> responseSet = roomRateResponses.stream()
                        .collect(Collectors.toMap(RoomRateResponse::getInnsistRoomRateId, RoomRateResponse::getInvoiceId));

        importRoomRates.forEach(importRoomRate -> {
            if(responseSet.containsKey(importRoomRate.getRoomRate().getId())){
                importRoomRate.setUpdatedAt(LocalDateTime.now());
                importRoomRate.setErrorMessage("InvoiceId: " + responseSet.get(importRoomRate.getRoomRate().getId()));
                updateRoomRateStatus(importRoomRate.getRoomRate(), responseSet.get(importRoomRate.getRoomRate().getId()), RoomRateStatus.PROCESSED);
            }
        });

        importRoomRateService.updateMany(importRoomRates);

        List<RoomRateDto> roomRates = importRoomRates.stream()
                .map(ImportRoomRateDto::getRoomRate)
                .toList();

        updateRoomRates(roomRates);
    }

    private void updateRoomRates(List<RoomRateDto> roomRates){
        roomRateService.updateMany(roomRates);
    }

    private void saveBookingsResponseWhenFailed(UUID importProcessId, List<RoomRateResponse> errorResponses) {
        List<ImportRoomRateDto> importRoomRates = importRoomRateService.findByImportProcessId(importProcessId);

        Map<UUID, List<RoomRateFieldError>> errorMap = errorResponses.stream()
                .collect(Collectors.toMap(RoomRateResponse::getInnsistRoomRateId, RoomRateResponse::getErrors));

        importRoomRates.forEach(importRoomRate -> {
            importRoomRate.setUpdatedAt(LocalDateTime.now());
            if(errorMap.containsKey(importRoomRate.getRoomRate().getId())){
                importRoomRate.setErrorMessage(formatErrorField(errorMap.get(importRoomRate.getRoomRate().getId())));
                updateRoomRateStatus(importRoomRate.getRoomRate(), null, RoomRateStatus.FAILED);
            }else{
                updateRoomRateStatus(importRoomRate.getRoomRate(), null, RoomRateStatus.PENDING);
            }
        });

        importRoomRateService.updateMany(importRoomRates);

        List<RoomRateDto> roomRates = importRoomRates.stream()
                .map(ImportRoomRateDto::getRoomRate)
                .toList();
        roomRateService.updateMany(roomRates);
    }

    private void updateRoomRateStatus(RoomRateDto roomRate, UUID invoiceId, RoomRateStatus status){
        roomRate.setInvoiceId(invoiceId);
        roomRate.setUpdatedAt(LocalDateTime.now());
        roomRate.setStatus(status);
    }

    private String formatErrorField(List<RoomRateFieldError> errorFields){
        return errorFields.stream()
                .map(errorField -> errorField.getField() + ": " + errorField.getMessageError())
                .collect(Collectors.joining("|"));
    }
    private void updateImportProcessStatus(ImportProcessDto importProcess, ImportProcessStatus status, int totalFailed){
        importProcess.setStatus(status);
        importProcess.setCompletedAt(LocalDateTime.now());
        importProcess.setTotalSuccessful(importProcess.getTotalBookings() - totalFailed);
        importProcess.setTotalFailed(totalFailed);
        importProcessService.update(importProcess);
    }

    private int getTotalFailed(UUID processId){
        List<ImportRoomRateDto> roomRates = importRoomRateService.findByImportProcessId(processId);
        return roomRates.stream()
                .filter(importRoomRate ->  importRoomRate.getRoomRate().getStatus().equals(RoomRateStatus.FAILED))
                .toList().size();
    }
}