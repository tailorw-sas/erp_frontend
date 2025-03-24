package com.kynsoft.finamer.insis.application.command.roomRate.createGrouped;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.services.*;
import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomCategory;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CreateGroupedRatesCommandHandler implements ICommandHandler<CreateGroupedRatesCommand> {

    private final IRoomRateService service;
    private final IManageHotelService manageHotelService;
    private final IManageAgencyService manageAgencyService;
    private final IManageRoomTypeService manageRoomTypeService;
    private final IManageRatePlanService manageRatePlanService;
    private final IManageRoomCategoryService manageRoomCategoryService;
    private final IBatchProcessLogService logService;

    public CreateGroupedRatesCommandHandler(IRoomRateService service,
                                            IManageHotelService manageHotelService,
                                            IManageAgencyService manageAgencyService,
                                            IManageRoomTypeService manageRoomTypeService,
                                            IManageRatePlanService manageRatePlanService,
                                            IManageRoomCategoryService manageRoomCategoryService,
                                            IBatchProcessLogService logService){
        this.service = service;
        this.manageHotelService = manageHotelService;
        this.manageAgencyService = manageAgencyService;
        this.manageRoomTypeService = manageRoomTypeService;
        this.manageRatePlanService = manageRatePlanService;
        this.manageRoomCategoryService = manageRoomCategoryService;
        this.logService = logService;
    }

    @Override
    public void handle(CreateGroupedRatesCommand command) {
        boolean processed;
        ManageHotelDto hotelDto = manageHotelService.findByCode(command.getHotel());

        List<RoomRateDto> currentRoomRates = service.findByHotelAndInvoiceDate(hotelDto.getId(), command.getInvoiceDate());

        if(currentRoomRates.isEmpty()) {
            processed = processNewRates(command, hotelDto);
        }else{
            processed = processExistingBooking(command, currentRoomRates, hotelDto);
        }

        updateLog(command.getId(), command.getRoomRateCommandList().size(), processed ? command.getRoomRateCommandList().size() : 0);
    }

    private void updateLog(UUID id, int totalRecordsRead, int totalRecordsProcessed){
        BatchProcessLogDto log = logService.findById(id);
        log.setTotalRecordsRead(log.getTotalRecordsRead() + totalRecordsRead);
        log.setTotalRecordsProcessed(log.getTotalRecordsProcessed() + totalRecordsProcessed);

        logService.update(log);
    }

    private BatchProcessLogDto getBatchLog(UUID id){
        return logService.findById(id);
    }

    private Boolean processNewRates(CreateGroupedRatesCommand command, ManageHotelDto hotelDto){
        createRates(hotelDto, command.getRoomRateCommandList());
        return true;
    }

    private boolean processExistingBooking(CreateGroupedRatesCommand command, List<RoomRateDto> currentRoomRates, ManageHotelDto hotelDto){
        if (shouldCancelRoomRate(currentRoomRates, command.getRoomRateCommandList())) {
            cancelRates(currentRoomRates);
            createRates(hotelDto, command.getRoomRateCommandList());
            return true;
        }

        return false;
    }

    private void createRates(ManageHotelDto hotel, List<CreateRoomRateCommand> rateCommands){
        Map<String, ManageAgencyDto> agenciesMap = getAgencies(rateCommands);
        Map<String, ManageRoomTypeDto> roomTypesMap = getRoomTypes(rateCommands, hotel);
        Map<String, ManageRatePlanDto> ratePlansMap = getRatePlans(rateCommands, hotel);
        Map<String, ManageRoomCategoryDto> roomCategoriesMap = getRoomCategories(rateCommands);

        List<RoomRateDto> newRates = rateCommands.stream()
                        .map(command -> buildRoomRateDto(command,
                                hotel,
                                agenciesMap.get(command.getAgency()),
                                roomTypesMap.get(command.getRoomType()),
                                ratePlansMap.get(command.getRatePlan()),
                                roomCategoriesMap.get(command.getRoomCategory())
                                )).toList();

        service.createMany(newRates);
    }

    private boolean shouldCancelRoomRate(List<RoomRateDto> currentRates, List<CreateRoomRateCommand> newRates){
        if(currentRates.size() != newRates.size()){
            return true;
        }
        Set<String> existingHashes = currentRates.stream()
                .map(RoomRateDto::getHash)
                .collect(Collectors.toSet());

        return newRates.stream()
                .map(CreateRoomRateCommand::getHash)
                .anyMatch(hash -> !existingHashes.contains(hash));
    }

    private void cancelRates(List<RoomRateDto> ratesToCancell){
        ratesToCancell.forEach(rate -> {
            if(!rate.getStatus().equals(RoomRateStatus.PROCESSED)){
                rate.setStatus(RoomRateStatus.DELETED);
                rate.setUpdatedAt(LocalDateTime.now());
            }
        });
        service.updateMany(ratesToCancell);
    }

    private Map<String, ManageAgencyDto> getAgencies(List<CreateRoomRateCommand> commands){
        List<String> agencyCodes = commands.stream()
                        .map(command -> command.getAgency().trim())
                        .distinct()
                        .toList();
        return manageAgencyService.findByCodes(agencyCodes).stream()
                .collect(Collectors.toMap(ManageAgencyDto::getCode, agency -> agency));
    }

    private Map<String, ManageRatePlanDto> getRatePlans(List<CreateRoomRateCommand> commands, ManageHotelDto hotel){
        List<String> ratePlanCodes = commands.stream()
                .map(command -> command.getRatePlan().trim())
                .distinct()
                .toList();

        return manageRatePlanService.findAllByCodesAndHotel(ratePlanCodes, hotel.getId()).stream()
                .collect(Collectors.toMap(ManageRatePlanDto::getCode, ratePlan -> ratePlan));
    }

    private Map<String, ManageRoomTypeDto> getRoomTypes(List<CreateRoomRateCommand> commands, ManageHotelDto hotel){
        List<String> roomTypesCodes = commands.stream()
                .map(command -> command.getRoomType().trim())
                .distinct()
                .toList();
        return manageRoomTypeService.findAllByCodesAndHotel(roomTypesCodes, hotel.getId()).stream()
                .collect(Collectors.toMap(ManageRoomTypeDto::getCode, roomType -> roomType));
    }

    private Map<String, ManageRoomCategoryDto> getRoomCategories(List<CreateRoomRateCommand> commands){
        List<String> roomCategoriesCodes = commands.stream()
                .map(command -> command.getRoomCategory().trim())
                .distinct()
                .toList();
        return manageRoomCategoryService.findAllByCodes(roomCategoriesCodes).stream()
                .collect(Collectors.toMap(ManageRoomCategoryDto::getCode, roomCategory -> roomCategory));
    }

    private RoomRateDto buildRoomRateDto(CreateRoomRateCommand createRoomRateCommand,
                                         ManageHotelDto hotelDto,
                                         ManageAgencyDto agency,
                                         ManageRoomTypeDto roomType,
                                         ManageRatePlanDto ratePlan,
                                         ManageRoomCategoryDto roomCategory){
        return new RoomRateDto(
                UUID.randomUUID(),
                RoomRateStatus.PENDING,
                hotelDto,
                null,
                createRoomRateCommand.getAgency(),
                agency,
                createRoomRateCommand.getCheckInDate(),
                createRoomRateCommand.getCheckOutDate(),
                createRoomRateCommand.getStayDays(),
                createRoomRateCommand.getReservationCode(),
                createRoomRateCommand.getGuestName(),
                createRoomRateCommand.getFirstName(),
                createRoomRateCommand.getLastName(),
                createRoomRateCommand.getAmount(),
                createRoomRateCommand.getRoomType(),
                roomType,
                createRoomRateCommand.getCouponNumber(),
                createRoomRateCommand.getTotalNumberOfGuest(),
                createRoomRateCommand.getAdults(),
                createRoomRateCommand.getChildrens(),
                createRoomRateCommand.getRatePlan(),
                ratePlan,
                createRoomRateCommand.getInvoicingDate(),
                createRoomRateCommand.getHotelCreationDate(),
                createRoomRateCommand.getOriginalAmount(),
                createRoomRateCommand.getAmountPaymentApplied(),
                createRoomRateCommand.getRateByAdult(),
                createRoomRateCommand.getRateByChild(),
                createRoomRateCommand.getRemarks(),
                createRoomRateCommand.getRoomNumber(),
                createRoomRateCommand.getHotelInvoiceAmount() == 0 || createRoomRateCommand.getQuote() == 0 ? 0
                        : Math.round((createRoomRateCommand.getHotelInvoiceAmount() / createRoomRateCommand.getQuote()) * 100.0) /100.0,
                createRoomRateCommand.getHotelInvoiceNumber(),
                createRoomRateCommand.getInvoiceFolioNumber(),
                createRoomRateCommand.getQuote(),
                createRoomRateCommand.getRenewalNumber(),
                createRoomRateCommand.getHash(),
                createRoomRateCommand.getRoomCategory(),
                roomCategory,
                null,
                null
        );
    }

    public static List<CreateRoomRateCommand> findRecordsWithDifferentHash(List<RoomRateDto> roomRateDtos, List<CreateRoomRateCommand> createRoomRateCommands) {
        Set<String> roomRateDtoHashes = roomRateDtos.stream()
                .map(RoomRateDto::getHash)
                .collect(Collectors.toSet());

        return createRoomRateCommands.stream()
                .filter(command -> !roomRateDtoHashes.contains(command.getHash()))
                .collect(Collectors.toList());
    }
}
