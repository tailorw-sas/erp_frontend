package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.rate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.batchProcessLog.create.CreateBatchProcessLogCommand;
import com.kynsoft.finamer.insis.application.command.batchProcessLog.update.UpdateBatchProcessLogCommand;
import com.kynsoft.finamer.insis.application.command.manageRatePlan.create.CreateRatePlanCommand;
import com.kynsoft.finamer.insis.application.command.manageRatePlan.createMany.CreateManyManageRatePlanCommand;
import com.kynsoft.finamer.insis.application.command.manageRoomCategory.create.CreateManageRoomCategoryCommand;
import com.kynsoft.finamer.insis.application.command.manageRoomCategory.createMany.CreateManyManageRoomCategoryCommand;
import com.kynsoft.finamer.insis.application.command.manageRoomType.create.CreateRoomTypeCommand;
import com.kynsoft.finamer.insis.application.command.manageRoomType.createMany.CreateManyManageRoomTypeCommand;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.insis.application.command.roomRate.createGrouped.CreateGroupedRatesCommand;
import com.kynsoft.finamer.insis.application.query.manageHotel.getByCode.GetManageHotelByCodeQuery;
import com.kynsoft.finamer.insis.application.query.manageRatePlan.getIdsByCodes.GetManageRatePlanIdsByCodesQuery;
import com.kynsoft.finamer.insis.application.query.manageRoomCategory.getIdsByCodes.GetManageRoomCategoryIdsByCodesQuery;
import com.kynsoft.finamer.insis.application.query.manageRoomType.getIdsByCodes.GetManageRoomTypeIdsByCodesQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRatePlan.ManageRatePlanIdsResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomCategory.ManageRoomCategoryIdsResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType.ManageRoomTypeIdsResponse;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.BookingKafka;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.GroupedRatesKafka;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ManageRateKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class ConsumerReplicateGroupedRatesService {

    private final IMediator mediator;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ConsumerReplicateGroupedRatesService(IMediator mediator){
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-grouped-rate", groupId = "innsist-entity-replica")
    public void listen(String message){
        try{
            ObjectMapper mapper = new ObjectMapper();
            GroupedRatesKafka objKafka = mapper.readValue(message, new TypeReference<GroupedRatesKafka>() {});

            createLog(objKafka.getLogId(), objKafka.getHotelCode(), LocalDate.parse(objKafka.getInvoiceDate(), DATE_FORMATTER), LocalDate.parse(objKafka.getInvoiceDate(), DATE_FORMATTER), objKafka.getProcessId());

            UUID hotelId = getHotelIdFromCode(objKafka.getHotelCode());

            processNewRatePlans(objKafka.getManageRateKafkaList(), hotelId);
            processNewRoomTypes(objKafka.getManageRateKafkaList(), hotelId);
            processNewRoomCategories(objKafka.getManageRateKafkaList());

            CreateGroupedRatesCommand command = new CreateGroupedRatesCommand(
                    objKafka.getLogId(),
                    objKafka.getHotelCode(),
                    LocalDate.parse(objKafka.getInvoiceDate(), DATE_FORMATTER),
                    objKafka.getManageRateKafkaList().stream()
                            .map(this::rateKafkaToCommand)
                            .toList()
            );
            mediator.send(command);

            setLogAsCompleted(objKafka.getLogId(), objKafka.getHotelCode());
        }catch (Exception ex){
            Logger.getLogger(ConsumerReplicateGroupedRatesService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createLog(UUID id, String hotel, LocalDate startDate, LocalDate endDate, UUID processId){
        CreateBatchProcessLogCommand command = new CreateBatchProcessLogCommand(id, BatchType.AUTOMATIC.name(), BatchStatus.START.name(), LocalDateTime.now(), hotel, startDate, endDate, processId);
        mediator.send(command);
    }

    private void setLogAsCompleted(UUID idLog, String hotel){
        UpdateBatchProcessLogCommand command = new UpdateBatchProcessLogCommand(idLog, BatchStatus.END.name(), hotel, LocalDateTime.now(), 0, 0);
        mediator.send(command);
    }

    private void processNewRatePlans(List<ManageRateKafka> newRateKafkaList, UUID hotel){
        List<String> newRatePlansCodes = newRateKafkaList.stream()
                .map(ManageRateKafka::getRatePlanCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .toList();

        Map<String, UUID> newRatePlansCodesIds = getRatePlansIdsByCodes(hotel, newRatePlansCodes);

        List<String> missingRatePlans = newRatePlansCodes.stream()
                .filter(ratePlan -> !newRatePlansCodesIds.containsKey(ratePlan))
                .toList();

        if(!missingRatePlans.isEmpty()){
            List<CreateRatePlanCommand> newRatePlans = missingRatePlans.stream()
                    .map(ratePlanCode -> new CreateRatePlanCommand(
                            UUID.randomUUID(),
                            ratePlanCode,
                            ratePlanCode,
                            hotel
                    ))
                    .toList();
            CreateManyManageRatePlanCommand command = new CreateManyManageRatePlanCommand(hotel, newRatePlans);
            mediator.send(command);
        }

    }

    private void processNewRoomTypes(List<ManageRateKafka> newRatesKakfaList, UUID hotel){
        List<String> newRoomTypes = newRatesKakfaList.stream()
                .map(ManageRateKafka::getRoomTypeCode)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .toList();

        Map<String, UUID> newRoomTypeCodeIds = getRoomTypeIdsByCodes(hotel, newRoomTypes);

        List<String> missingRoomTypes = newRoomTypes.stream()
                .filter(roomType -> !newRoomTypeCodeIds.containsKey(roomType))
                .toList();

        if(!missingRoomTypes.isEmpty()){
            List<CreateRoomTypeCommand> newRoomTypesCommand = missingRoomTypes.stream()
                    .map(newRoomTpe -> new CreateRoomTypeCommand(
                            UUID.randomUUID(),
                            newRoomTpe,
                            newRoomTpe,
                            hotel
                    ))
                    .toList();

            CreateManyManageRoomTypeCommand command = new CreateManyManageRoomTypeCommand(hotel, newRoomTypesCommand);
            mediator.send(command);
        }
    }

    private void processNewRoomCategories(List<ManageRateKafka> newRatesKafkaList){
        List<String> newRoomCategories = newRatesKafkaList.stream()
                .map(ManageRateKafka::getRoomCategory)
                .filter(Objects::nonNull)
                .map(String::trim)
                .distinct()
                .toList();

        Map<String, UUID> newRoomCategoryCodeIds = getRoomCategoryIdsByCodes(newRoomCategories);

        List<String> missingRoomCategories = newRoomCategories.stream()
                .filter(roomCategory -> !newRoomCategoryCodeIds.containsKey(roomCategory))
                .toList();

        if(!missingRoomCategories.isEmpty()){
            List<CreateManageRoomCategoryCommand> newRoomCategoryCommands = missingRoomCategories.stream()
                    .map(newRoomCategory -> new CreateManageRoomCategoryCommand(
                            UUID.randomUUID(),
                            newRoomCategory,
                            newRoomCategory,
                            "ACTIVE"
                    ))
                    .toList();
            CreateManyManageRoomCategoryCommand command = new CreateManyManageRoomCategoryCommand(newRoomCategoryCommands);
            mediator.send(command);
        }
    }

    private UUID getHotelIdFromCode(String hotelCode){
        GetManageHotelByCodeQuery hotelQuery = new GetManageHotelByCodeQuery(hotelCode);
        ManageHotelResponse hotelResponse = mediator.send(hotelQuery);

        if(Objects.isNull(hotelResponse)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Hotel not found.")));
        }
        return  hotelResponse.getId();
    }

    private Map<String, UUID> getRatePlansIdsByCodes(UUID hotel, List<String> codes){
        GetManageRatePlanIdsByCodesQuery query = new GetManageRatePlanIdsByCodesQuery(hotel, codes);
        ManageRatePlanIdsResponse response = mediator.send(query);

        return response.getIds();
    }

    private Map<String, UUID> getRoomTypeIdsByCodes(UUID hotel, List<String> codes){
        GetManageRoomTypeIdsByCodesQuery query = new GetManageRoomTypeIdsByCodesQuery(hotel, codes);
        ManageRoomTypeIdsResponse response = mediator.send(query);

        return  response.getIds();
    }

    private Map<String, UUID> getRoomCategoryIdsByCodes(List<String> codes){
        GetManageRoomCategoryIdsByCodesQuery query = new GetManageRoomCategoryIdsByCodesQuery(codes);
        ManageRoomCategoryIdsResponse response = mediator.send(query);

        return  response.getIds();
    }

    private CreateRoomRateCommand rateKafkaToCommand(ManageRateKafka objKafka){
        return  new CreateRoomRateCommand(
                objKafka.getHotelCode(),
                objKafka.getAgencyCode(),
                LocalDate.parse(objKafka.getCheckInDate(), DATE_FORMATTER),
                LocalDate.parse(objKafka.getCheckOutDate(), DATE_FORMATTER),
                objKafka.getStayDays(),
                objKafka.getReservationCode(),
                objKafka.getGuestName(),
                objKafka.getFirstName(),
                objKafka.getLastName(),
                objKafka.getAmount(),
                objKafka.getRoomTypeCode(),
                objKafka.getCouponNumber(),
                objKafka.getTotalNumberOfGuest(),
                objKafka.getAdults(),
                objKafka.getChildrens(),
                objKafka.getRatePlanCode(),
                LocalDate.parse(objKafka.getInvoicingDate(), DATE_FORMATTER),
                LocalDate.parse(objKafka.getHotelCreationDate(), DATE_FORMATTER),
                objKafka.getOriginalAmount(),
                objKafka.getAmountPaymentApplied(),
                objKafka.getRateByAdult(),
                objKafka.getRateByChild(),
                objKafka.getRemarks(),
                objKafka.getRoomNumber(),
                objKafka.getHotelInvoiceAmount(),
                objKafka.getHotelInvoiceNumber(),
                objKafka.getInvoiceFolioNumber(),
                objKafka.getQuote(),
                objKafka.getRenewalNumber(),
                objKafka.getRoomCategory(),
                objKafka.getHash()
        );
    }
}
