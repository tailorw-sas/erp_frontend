package com.kynsoft.finamer.insis.application.command.roomRate.createGrouped;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.exceptions.InnsistRateSincronizationException;
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
            processed = this.processNewRates(command, hotelDto, command.getId());
        }else{
            processed = this.processExistingBooking(command, currentRoomRates, hotelDto, command.getId());
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

    private Boolean processNewRates(CreateGroupedRatesCommand command, ManageHotelDto hotelDto, UUID processId){
        this.createRates(hotelDto, command.getRoomRateCommandList(), processId);
        return true;
    }

    private boolean processExistingBooking(CreateGroupedRatesCommand command, List<RoomRateDto> currentRoomRates, ManageHotelDto hotelDto, UUID processId){
        List<RoomRateDto> pendingRoomRates = currentRoomRates.stream()
                .filter(roomRateDto -> roomRateDto.getStatus() != RoomRateStatus.PROCESSED)
                .collect(Collectors.toList());

        if (shouldCancelRoomRate(pendingRoomRates, command.getRoomRateCommandList())) {
            cancelRates(pendingRoomRates);
            createRates(hotelDto, command.getRoomRateCommandList(), processId);
            return true;
        }

        return false;
    }

    private void createRates(ManageHotelDto hotel, List<CreateRoomRateCommand> rateCommands, UUID processId){
        Map<String, ManageAgencyDto> agenciesMap = new HashMap<>();
        Map<String, ManageRoomTypeDto> roomTypesMap = new HashMap<>();
        Map<String, ManageRatePlanDto> ratePlansMap = new HashMap<>();
        Map<String, ManageRoomCategoryDto> roomCategoriesMap = new HashMap<>();
        this.getMaps(hotel, rateCommands, agenciesMap, roomTypesMap, ratePlansMap, roomCategoriesMap);

        List<RoomRateDto> newRates = rateCommands.stream()
                        .map(command -> buildRoomRateDto(command,
                                hotel,
                                this.getAgencyFromMap(command.getAgency(), agenciesMap, processId),
                                this.getRoomTypeFromMap(command.getRoomType(), roomTypesMap, hotel, processId),
                                this.getRatePlanFromMap(command.getRatePlan(), ratePlansMap, hotel, processId),
                                this.getRoomCategoryFromMap(command.getRoomCategory(), roomCategoriesMap, processId)
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

    private void getMaps(ManageHotelDto hotel, List<CreateRoomRateCommand> rateCommands,
                         Map<String, ManageAgencyDto> agenciesMap,
                         Map<String, ManageRoomTypeDto> roomTypesMap,
                         Map<String, ManageRatePlanDto> ratePlansMap,
                         Map<String, ManageRoomCategoryDto> roomCategoriesMap){
        Set<String> agencyCodeSet = new HashSet<>();
        Set<String> roomTypeCodeSet = new HashSet<>();
        Set<String> ratePlanCodeSet = new HashSet<>();
        Set<String> roomCategoryCodeSet = new HashSet<>();

        for(CreateRoomRateCommand command : rateCommands){
            if(command.getAgency() != null && !command.getAgency().isEmpty()) agencyCodeSet.add(command.getAgency());
            if(command.getRoomType() != null && !command.getRoomType().isEmpty()) roomTypeCodeSet.add(command.getRoomType());
            if(command.getRatePlan() != null && !command.getRatePlan().isEmpty()) ratePlanCodeSet.add(command.getRatePlan());
            if(command.getRoomCategory() != null && !command.getRoomCategory().isEmpty()) roomCategoryCodeSet.add(command.getRoomCategory());
        }

        agenciesMap.putAll(this.getAgencies(new ArrayList<>(agencyCodeSet)));
        roomTypesMap.putAll(this.getRoomTypes(new ArrayList<>(roomTypeCodeSet), hotel));
        ratePlansMap.putAll(this.getRatePlans(new ArrayList<>(ratePlanCodeSet), hotel));
        roomCategoriesMap.putAll(this.getRoomCategories(new ArrayList<>(roomCategoryCodeSet)));
    }

    private Map<String, ManageAgencyDto> getAgencies(List<String> agencyCodes){
        return manageAgencyService.findByCodes(agencyCodes).stream()
                .collect(Collectors.toMap(ManageAgencyDto::getCode, agency -> agency));
    }

    private Map<String, ManageRatePlanDto> getRatePlans(List<String> ratePlanCodes, ManageHotelDto hotel){
        return manageRatePlanService.findAllByCodesAndHotel(ratePlanCodes, hotel.getId()).stream()
                .collect(Collectors.toMap(ManageRatePlanDto::getCode, ratePlan -> ratePlan));
    }

    private Map<String, ManageRoomTypeDto> getRoomTypes(List<String> roomTypesCodes, ManageHotelDto hotel){
        return manageRoomTypeService.findAllByCodesAndHotel(roomTypesCodes, hotel.getId()).stream()
                .collect(Collectors.toMap(ManageRoomTypeDto::getCode, roomType -> roomType));
    }

    private Map<String, ManageRoomCategoryDto> getRoomCategories(List<String> roomCategoriesCodes){
        return manageRoomCategoryService.findAllByCodes(roomCategoriesCodes).stream()
                .collect(Collectors.toMap(ManageRoomCategoryDto::getCode, roomCategory -> roomCategory));
    }

    private ManageAgencyDto getAgencyFromMap(String agencyCode, Map<String, ManageAgencyDto> agencyMap, UUID processId){
        if(agencyCode == null || agencyCode.isEmpty()){
            throw new InnsistRateSincronizationException(processId, String.format("The agency code %s must not be null or empty", agencyCode));
        }

        if(agencyMap == null || agencyMap.isEmpty()){
            throw new InnsistRateSincronizationException(processId, "The agency map must not be null or empty");
        }

        if(agencyMap.containsKey(agencyCode)){
            return agencyMap.get(agencyCode);
        }

        throw new InnsistRateSincronizationException(processId, String.format("The agency code %s not found", agencyCode));
    }

    private ManageRoomTypeDto getRoomTypeFromMap(String roomTypeCode, Map<String, ManageRoomTypeDto> roomTypeMap, ManageHotelDto hotelDto, UUID processId){
        if(roomTypeCode == null || roomTypeCode.isEmpty()){
            throw new InnsistRateSincronizationException(processId, String.format("The room type code %s must not be null or empty", roomTypeCode));
        }

        if(roomTypeMap == null || roomTypeMap.isEmpty()){
            throw new InnsistRateSincronizationException(processId, "The room type map must not be null or empty");
        }

        if(roomTypeMap.containsKey(roomTypeCode)){
            return roomTypeMap.get(roomTypeCode);
        }

        throw new InnsistRateSincronizationException(processId, String.format("The room type code %s not found for the hotel %s", roomTypeMap, hotelDto));
    }

    private ManageRatePlanDto getRatePlanFromMap(String ratePlaCode, Map<String, ManageRatePlanDto> ratePlanMap, ManageHotelDto hotelDto, UUID processId){
        if(ratePlaCode == null || ratePlaCode.isEmpty()){
            throw new InnsistRateSincronizationException(processId, String.format("The rate plan code %s must not be null or empty", ratePlaCode));
        }

        if(ratePlanMap == null || ratePlanMap.isEmpty()){
            throw new InnsistRateSincronizationException(processId, "The rate plan map must not be null or empty");
        }

        if(ratePlanMap.containsKey(ratePlaCode)){
            return ratePlanMap.get(ratePlaCode);
        }

        throw new InnsistRateSincronizationException(processId, String.format("The rate plan code %s not found for the hotel %s", ratePlaCode, hotelDto));
    }

    private ManageRoomCategoryDto getRoomCategoryFromMap(String roomCategoryCode, Map<String, ManageRoomCategoryDto> roomCategoryMap, UUID processId){
        if(roomCategoryCode == null || roomCategoryCode.isEmpty()){
            throw new InnsistRateSincronizationException(processId, String.format("The room category code %s must not be null or empty", roomCategoryCode));
        }

        if(roomCategoryMap == null || roomCategoryMap.isEmpty()){
            throw new InnsistRateSincronizationException(processId, "The room category map must not be null or empty");
        }

        if(roomCategoryMap.containsKey(roomCategoryCode)){
            return roomCategoryMap.get(roomCategoryCode);
        }

        throw new InnsistRateSincronizationException(processId, String.format("The room category code %s not found", roomCategoryCode));
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
