package com.kynsoft.finamer.insis.application.services.roomRate.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomCategoryKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.domain.rules.ValidateStringNotEmptyRule;
import com.kynsoft.finamer.insis.application.services.batchLog.BatchLogService;
import com.kynsoft.finamer.insis.application.services.manageRatePlan.create.CreateManageRatePlanService;
import com.kynsoft.finamer.insis.application.services.manageRoomCategory.create.CreateManageRoomCategoryService;
import com.kynsoft.finamer.insis.application.services.manageRoomType.create.CreateManageRoomTypeService;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.exceptions.InnsistRateSincronizationException;
import com.kynsoft.finamer.insis.domain.services.*;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRatePlan.ProducerReplicateManageRatePlanService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRoomCategory.ProducerReplicateManageRoomCategoryService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageRoomType.ProducerReplicateManageRoomTypeService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CreateRoomRatesService {

    private final CreateManageRatePlanService createManageRatePlanService;
    private final CreateManageRoomTypeService createManageRoomTypeService;
    private final CreateManageRoomCategoryService createManageRoomCategoryService;
    private final ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService;
    private final ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService;
    private final ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService;
    private final IManageHotelService manageHotelService;
    private final IRoomRateService service;

    private final IManageAgencyService manageAgencyService;
    private final IManageRoomTypeService manageRoomTypeService;
    private final IManageRatePlanService manageRatePlanService;
    private final IManageRoomCategoryService manageRoomCategoryService;
    private final BatchLogService batchLogService;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final ExecutorService loadCatalogsExecutor = Executors.newFixedThreadPool(4);

    private static final Logger log = LoggerFactory.getLogger(CreateRoomRatesService.class);

    public CreateRoomRatesService(CreateManageRatePlanService createManageRatePlanService,
                                  CreateManageRoomTypeService createManageRoomTypeService,
                                  CreateManageRoomCategoryService createManageRoomCategoryService,
                                  ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService,
                                  ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService,
                                  ProducerReplicateManageRoomCategoryService producerReplicateManageRoomCategoryService,
                                  IManageHotelService manageHotelService,
                                  IRoomRateService service,
                                  IManageAgencyService manageAgencyService,
                                  IManageRoomTypeService manageRoomTypeService,
                                  IManageRatePlanService manageRatePlanService,
                                  IManageRoomCategoryService manageRoomCategoryService,
                                  BatchLogService batchLogService){
        this.createManageRatePlanService = createManageRatePlanService;
        this.createManageRoomTypeService = createManageRoomTypeService;
        this.createManageRoomCategoryService = createManageRoomCategoryService;

        this.producerReplicateManageRatePlanService = producerReplicateManageRatePlanService;
        this.producerReplicateManageRoomTypeService = producerReplicateManageRoomTypeService;
        this.producerReplicateManageRoomCategoryService = producerReplicateManageRoomCategoryService;

        this.manageHotelService = manageHotelService;

        this.service = service;

        this.manageAgencyService = manageAgencyService;
        this.manageRoomTypeService = manageRoomTypeService;
        this.manageRatePlanService = manageRatePlanService;
        this.manageRoomCategoryService = manageRoomCategoryService;
        this.batchLogService = batchLogService;

    }

    public void createRoomRates(UUID processId,
                                String hotel,
                                LocalDate invoiceDate,
                                BatchType batchType,
                                List<CreateRoomRateRequest> createRoomRates){
        ManageHotelDto hotelDto = this.manageHotelService.findByCode(hotel);
        this.createRoomRates(processId,
                hotelDto,
                invoiceDate,
                batchType,
                createRoomRates);
    }

    public void createRoomRates(UUID processId,
                                ManageHotelDto hotelDto,
                                LocalDate invoiceDate,
                                BatchType batchType,
                                List<CreateRoomRateRequest> createRoomRates){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(hotelDto.getCode(), "hotel", "The hotel code must not be null"));
        RulesChecker.checkRule(new ValidateStringNotEmptyRule(hotelDto.getCode(), "hotel", "The hotel code must not be empty"));

        log.info("[{}] Iniciando creación de room rates para hotel '{}', fecha {}, tipo '{}'. Total a crear: {}",
                processId, hotelDto.getCode(), invoiceDate, batchType.name(), createRoomRates.size());

        BatchProcessLogDto processLog = this.createLog(hotelDto.getCode(), invoiceDate, invoiceDate, processId, batchType);

        this.updateLogAsInProcess(processLog);
        try{
            if(!createRoomRates.isEmpty()){
                Instant before = Instant.now();
                this.processNewCatalogs(createRoomRates, hotelDto);
                Instant after = Instant.now();
                log.debug("************ Process New Catalogs" + Duration.between(before, after).toMillis()  + " ms");

                before = Instant.now();
                boolean processed = this.processRoomRates(processLog, invoiceDate, createRoomRates, hotelDto);
                after = Instant.now();
                log.debug("************ Process New Rates" + Duration.between(before, after).toMillis()  + " ms");

                this.updateLogAsCompleted(processLog, createRoomRates.size(), processed ? createRoomRates.size() : 0, null);
                log.info("[{}] Proceso completado correctamente para hotel '{}'", processId, hotelDto.getCode());
            }else{
                log.info("[{}] No se detectaron tarifas para crear. Proceso marcado como completado sin acción", processId);
                this.updateLogAsCompleted(processLog, 0, 0, null);
            }
        } catch (RuntimeException rex){
            log.error("[{}] Error en proceso de creación de tarifas: {}", processId, rex.getMessage(), rex);
            this.updateLogAsCompleted(processLog, createRoomRates.size(),  0, rex.getMessage());
        }
    }

    private BatchProcessLogDto createLog(String hotel, LocalDate startDate, LocalDate endDate, UUID processId, BatchType batchType){
        return batchLogService.createBatchLog(hotel, batchType, startDate, endDate, processId);
    }
    private void updateLogAsInProcess(BatchProcessLogDto processLog){
        processLog.setStatus(BatchStatus.PROCESS);
        batchLogService.updateBatchLog(processLog);
    }

    private void updateLogAsCompleted(BatchProcessLogDto processLog, int totalRecordsRead, int totalRecordsProcessed, String errorMessage){
        processLog.setTotalRecordsRead(processLog.getTotalRecordsRead() + totalRecordsRead);
        processLog.setTotalRecordsProcessed(processLog.getTotalRecordsProcessed() + totalRecordsProcessed);
        processLog.setCompletedAt(LocalDateTime.now());
        processLog.setStatus(BatchStatus.END);
        processLog.setErrorMessage(errorMessage);
        batchLogService.updateBatchLog(processLog);
    }

    private void processNewCatalogs(List<CreateRoomRateRequest> createRoomRates,
                                    ManageHotelDto hotelDto){
        Set<String> ratePlanCodeSet = new HashSet<>();
        Set<String> roomTypeCodeSet = new HashSet<>();
        Set<String> roomCategoryCodeSet = new HashSet<>();

        for(CreateRoomRateRequest createRoomRate : createRoomRates){
            if(Objects.nonNull(createRoomRate.getRatePlan())) ratePlanCodeSet.add(createRoomRate.getRatePlan());
            if(Objects.nonNull(createRoomRate.getRoomType())) roomTypeCodeSet.add(createRoomRate.getRoomType());
            if(Objects.nonNull(createRoomRate.getRoomCategory())) roomCategoryCodeSet.add(createRoomRate.getRoomCategory());
        }

        CompletableFuture<Void> futureRatePlans = CompletableFuture.runAsync(() -> this.processNewRatePlans(new ArrayList<>(ratePlanCodeSet), hotelDto));
        CompletableFuture<Void> futureRoomTypes = CompletableFuture.runAsync(() -> this.processNewRoomTypes(new ArrayList<>(roomTypeCodeSet), hotelDto));
        CompletableFuture<Void> futureRoomCategories = CompletableFuture.runAsync(() -> this.processNewRoomCategories(new ArrayList<>(roomCategoryCodeSet)));

        CompletableFuture.allOf(futureRatePlans, futureRoomTypes, futureRoomCategories).join();
    }

    private void processNewRatePlans(List<String> newRatePlanCodes, ManageHotelDto hotelDto){
        List<ManageRatePlanDto> newManageRatePlans = this.createManageRatePlanService.createManageRatePlans(newRatePlanCodes, hotelDto);
        newManageRatePlans.forEach(manageRatePlanDto -> {
                    producerReplicateManageRatePlanService.create(new ReplicateManageRatePlanKafka(manageRatePlanDto.getId(),
                            manageRatePlanDto.getHotel().getId(),
                            manageRatePlanDto.getCode(),
                            manageRatePlanDto.getName(),
                            manageRatePlanDto.getStatus()));
                }
        );
    }

    private void processNewRoomTypes(List<String> newRoomTypeCodes, ManageHotelDto hotelDto){
        List<ManageRoomTypeDto> newManageRoomTypes = this.createManageRoomTypeService.createManageRoomTypes(newRoomTypeCodes, hotelDto);

        newManageRoomTypes.forEach(
                manageRoomTypeDto -> {
                    producerReplicateManageRoomTypeService.create(new ReplicateManageRoomTypeKafka(
                            manageRoomTypeDto.getId(),
                            manageRoomTypeDto.getCode(),
                            manageRoomTypeDto.getName(),
                            manageRoomTypeDto.getStatus(),
                            manageRoomTypeDto.getHotel().getId()
                    ));
                }
        );
    }

    private void processNewRoomCategories(List<String> newRoomCategoryCodes){
        List<ManageRoomCategoryDto> newRoomCategories = this.createManageRoomCategoryService.createRoomCategories(newRoomCategoryCodes);

        newRoomCategories.forEach(
                manageRoomCategoryDto -> {
                    producerReplicateManageRoomCategoryService.create(new ReplicateManageRoomCategoryKafka(
                            manageRoomCategoryDto.getId(),
                            manageRoomCategoryDto.getCode(),
                            manageRoomCategoryDto.getName(),
                            manageRoomCategoryDto.getStatus()
                    ));
                }
        );
    }

    private boolean processRoomRates(BatchProcessLogDto processLog,
                                  LocalDate invoiceDate,
                                  List<CreateRoomRateRequest> createRoomRates,
                                  ManageHotelDto hotelDto){
        boolean processed;

        List<RoomRateDto> currentRoomRates = service.findByHotelAndInvoiceDate(hotelDto.getId(), invoiceDate);

        if(currentRoomRates.isEmpty()) {
            processed = this.processNewRates(createRoomRates, hotelDto, processLog.getProcessId());
        }else{
            processed = this.processExistingBooking(createRoomRates, currentRoomRates, hotelDto, processLog.getProcessId());
        }

        return processed;
    }

    private Boolean processNewRates(List<CreateRoomRateRequest> createRoomRates, ManageHotelDto hotelDto, UUID processId){
        this.createRates(hotelDto, createRoomRates, processId);
        return true;
    }

    private boolean processExistingBooking(List<CreateRoomRateRequest> createRoomRates, List<RoomRateDto> currentRoomRates, ManageHotelDto hotelDto, UUID processId){
        boolean shouldCancelRoomRate = this.shouldCancelRoomRate(currentRoomRates, createRoomRates);

        if (shouldCancelRoomRate) {
            cancelRates(currentRoomRates);
            createRates(hotelDto, createRoomRates, processId);
            return true;
        }

        return false;
    }

    private void createRates(ManageHotelDto hotel,
                             List<CreateRoomRateRequest> roomRates,
                             UUID processId){
        Map<String, ManageAgencyDto> agenciesMap = new HashMap<>();
        Map<String, ManageRoomTypeDto> roomTypesMap = new HashMap<>();
        Map<String, ManageRatePlanDto> ratePlansMap = new HashMap<>();
        Map<String, ManageRoomCategoryDto> roomCategoriesMap = new HashMap<>();
        this.getMaps(hotel, roomRates, agenciesMap, roomTypesMap, ratePlansMap, roomCategoriesMap);

        List<RoomRateDto> newRates = roomRates.stream()
                .map(command -> buildRoomRateDto(command,
                        hotel,
                        this.getAgencyFromMap(command.getAgency(), agenciesMap, processId),
                        this.getRoomTypeFromMap(command.getRoomType(), roomTypesMap, hotel, processId),
                        this.getRatePlanFromMap(command.getRatePlan(), ratePlansMap, hotel, processId),
                        this.getRoomCategoryFromMap(command.getRoomCategory(), roomCategoriesMap, processId)
                )).toList();

        service.createMany(newRates);
    }

    private boolean shouldCancelRoomRate(List<RoomRateDto> currentRates, List<CreateRoomRateRequest> createRoomRates){
        if(currentRates.isEmpty()){
            return false;
        }

        if(currentRates.size() != createRoomRates.size()){
            return true;
        }

        Set<String> existingHashes = currentRates.stream()
                .map(RoomRateDto::getHash)
                .collect(Collectors.toSet());

        return createRoomRates.stream()
                .map(CreateRoomRateRequest::getHash)
                .anyMatch(hash -> !existingHashes.contains(hash));
    }

    private void cancelRates(List<RoomRateDto> ratesToCancell){
        ratesToCancell.forEach(rate -> {
            if(rate.getStatus() != RoomRateStatus.PROCESSED ){
                rate.setStatus(RoomRateStatus.DELETED);
            }
            if(rate.getStatus() == RoomRateStatus.PROCESSED
                    || rate.getStatus() == RoomRateStatus.IN_PROCESS){
                rate.setStatus(RoomRateStatus.ANNULLED);
            }
            rate.setUpdatedAt(LocalDateTime.now());
        });
        service.updateMany(ratesToCancell);
    }

    private void getMaps(ManageHotelDto hotel,
                         List<CreateRoomRateRequest> roomRates,
                         Map<String, ManageAgencyDto> agenciesMap,
                         Map<String, ManageRoomTypeDto> roomTypesMap,
                         Map<String, ManageRatePlanDto> ratePlansMap,
                         Map<String, ManageRoomCategoryDto> roomCategoriesMap){
        Set<String> agencyCodeSet = new HashSet<>();
        Set<String> roomTypeCodeSet = new HashSet<>();
        Set<String> ratePlanCodeSet = new HashSet<>();
        Set<String> roomCategoryCodeSet = new HashSet<>();

        for(CreateRoomRateRequest command : roomRates){
            if(command.getAgency() != null && !command.getAgency().isEmpty()) agencyCodeSet.add(command.getAgency());
            if(command.getRoomType() != null && !command.getRoomType().isEmpty()) roomTypeCodeSet.add(command.getRoomType());
            if(command.getRatePlan() != null && !command.getRatePlan().isEmpty()) ratePlanCodeSet.add(command.getRatePlan());
            if(command.getRoomCategory() != null && !command.getRoomCategory().isEmpty()) roomCategoryCodeSet.add(command.getRoomCategory());
        }

        CompletableFuture<Void> futureAgencyMap = CompletableFuture.runAsync(() -> this.getAgencyMap(new ArrayList<>(agencyCodeSet), agenciesMap), loadCatalogsExecutor);
        CompletableFuture<Void> futureRatePlanMap = CompletableFuture.runAsync(() -> this.getRatePlanMap(new ArrayList<>(ratePlanCodeSet), hotel, ratePlansMap), loadCatalogsExecutor);
        CompletableFuture<Void> futureRoomTypeMap = CompletableFuture.runAsync(() -> this.getRoomTypeMap(new ArrayList<>(roomTypeCodeSet), hotel, roomTypesMap), loadCatalogsExecutor);
        CompletableFuture<Void> futureRoomCategoryMap = CompletableFuture.runAsync(() -> this.getRoomCategoryMap(new ArrayList<>(roomCategoryCodeSet), roomCategoriesMap), loadCatalogsExecutor);

        CompletableFuture.allOf(futureAgencyMap, futureRatePlanMap, futureRoomTypeMap, futureRoomCategoryMap).join();
    }

    private void getAgencyMap(List<String> agencyCodes, Map<String, ManageAgencyDto> agencyMap){
        agencyMap.putAll(this.getAgencies(agencyCodes).stream()
                .collect(Collectors.toMap(ManageAgencyDto::getCode, agency -> agency)));
    }

    private void getRatePlanMap(List<String> ratePlanCodes, ManageHotelDto hotel, Map<String, ManageRatePlanDto> ratePlanDtoMap){
        ratePlanDtoMap.putAll(this.getRatePlans(ratePlanCodes, hotel).stream()
                .collect(Collectors.toMap(ManageRatePlanDto::getCode, ratePlan -> ratePlan)));
    }

    private void getRoomTypeMap(List<String> roomTypesCodes, ManageHotelDto hotel, Map<String, ManageRoomTypeDto> roomTypeDtoMap){
        roomTypeDtoMap.putAll(this.getRoomTypes(roomTypesCodes, hotel).stream()
                .collect(Collectors.toMap(ManageRoomTypeDto::getCode, roomType -> roomType)));
    }

    private void getRoomCategoryMap(List<String> roomCategoriesCode, Map<String, ManageRoomCategoryDto> roomCategoryDtoMap){
        roomCategoryDtoMap.putAll(this.getRoomCategories(roomCategoriesCode).stream()
                .collect(Collectors.toMap(ManageRoomCategoryDto::getCode, roomCategory -> roomCategory)));
    }

    private List<ManageAgencyDto> getAgencies(List<String> agencyCodes){
        return manageAgencyService.findByCodes(agencyCodes);
    }

    private List<ManageRatePlanDto> getRatePlans(List<String> ratePlanCodes, ManageHotelDto hotel){
        return manageRatePlanService.findAllByCodesAndHotel(ratePlanCodes, hotel.getId());
    }

    private List<ManageRoomTypeDto> getRoomTypes(List<String> roomTypesCodes, ManageHotelDto hotel){
        return manageRoomTypeService.findAllByCodesAndHotel(roomTypesCodes, hotel.getId());
    }

    private List<ManageRoomCategoryDto> getRoomCategories(List<String> roomCategoriesCodes){
        return manageRoomCategoryService.findAllByCodes(roomCategoriesCodes);
    }

    private ManageAgencyDto getAgencyFromMap(String agencyCode, Map<String, ManageAgencyDto> agencyMap, UUID processId){
        if(agencyCode == null || agencyCode.isEmpty() || agencyMap == null || agencyMap.isEmpty()){
            return null;
        }

        if(agencyMap.containsKey(agencyCode)){
            return agencyMap.get(agencyCode);
        }

        return null;
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

    private RoomRateDto buildRoomRateDto(CreateRoomRateRequest createRoomRate,
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
                createRoomRate.getAgency(),
                agency,
                createRoomRate.getCheckInDate(),
                createRoomRate.getCheckOutDate(),
                createRoomRate.getStayDays(),
                createRoomRate.getReservationCode(),
                createRoomRate.getGuestName(),
                createRoomRate.getFirstName(),
                createRoomRate.getLastName(),
                createRoomRate.getAmount(),
                createRoomRate.getRoomType(),
                roomType,
                createRoomRate.getCouponNumber(),
                createRoomRate.getTotalNumberOfGuest(),
                createRoomRate.getAdults(),
                createRoomRate.getChildren(),
                createRoomRate.getRatePlan(),
                ratePlan,
                createRoomRate.getInvoicingDate(),
                createRoomRate.getHotelCreationDate(),
                createRoomRate.getOriginalAmount(),
                createRoomRate.getAmountPaymentApplied(),
                createRoomRate.getRateByAdult(),
                createRoomRate.getRateByChild(),
                createRoomRate.getRemarks(),
                createRoomRate.getRoomNumber(),
                createRoomRate.getHotelInvoiceAmount() == 0 || createRoomRate.getQuote() == 0 ? 0
                        : Math.round((createRoomRate.getHotelInvoiceAmount() / createRoomRate.getQuote()) * 100.0) /100.0,
                createRoomRate.getHotelInvoiceNumber(),
                createRoomRate.getInvoiceFolioNumber(),
                createRoomRate.getQuote(),
                createRoomRate.getRenewalNumber(),
                createRoomRate.getHash(),
                createRoomRate.getRoomCategory(),
                roomCategory,
                null,
                null,
                null
        );
    }

    @PreDestroy
    public void cleanUp() {
        shutdownExecutor("executor", executor);
        shutdownExecutor("loadCatalogsExecutor", loadCatalogsExecutor);
    }

    private void shutdownExecutor(String name, ExecutorService service) {
        service.shutdown();
        try {
            if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
                log.error("❗ [" + name + "] no terminó en 30 segundos. Forzando cierre...");
                List<Runnable> droppedTasks = service.shutdownNow();

                if (!droppedTasks.isEmpty()) {
                    log.error("⚠️ [" + name + "] Tareas no ejecutadas: " + droppedTasks.size());
                }
            } else {
                log.error("✅ [" + name + "] Todas las tareas finalizaron correctamente.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("🛑 [" + name + "] interrupción detectada. Forzando cierre inmediato...");
            service.shutdownNow();
        }
    }
}
