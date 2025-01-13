package com.kynsoft.finamer.insis.application.command.roomRate.createGrouped;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.services.*;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CreateGroupedRatesCommandHandler implements ICommandHandler<CreateGroupedRatesCommand> {

    private final IRoomRateService service;
    private final IBookingService bookingService;
    private final IManageHotelService manageHotelService;
    private final IManageAgencyService manageAgencyService;
    private final IManageRoomTypeService manageRoomTypeService;
    private final IManageRatePlanService manageRatePlanService;
    private final IManageRoomCategoryService roomCategoryService;
    private final IBatchProcessLogService logService;

    public CreateGroupedRatesCommandHandler(IRoomRateService service,
                                            IBookingService bookingService,
                                            IManageHotelService manageHotelService,
                                            IManageAgencyService manageAgencyService,
                                            IManageRoomTypeService manageRoomTypeService,
                                            IManageRatePlanService manageRatePlanService,
                                            IManageRoomCategoryService roomCategoryService,
                                            IBatchProcessLogService logService){
        this.service = service;
        this.bookingService = bookingService;
        this.manageHotelService = manageHotelService;
        this.manageAgencyService = manageAgencyService;
        this.manageRoomTypeService = manageRoomTypeService;
        this.manageRatePlanService = manageRatePlanService;
        this.roomCategoryService = roomCategoryService;
        this.logService = logService;
    }

    @Override
    public void handle(CreateGroupedRatesCommand command) {
        boolean processed;
        ManageHotelDto hotelDto = manageHotelService.findByCode(command.getHotel());
        BookingDto currentBookingDto = bookingService.findByTcaId(hotelDto, command.getInvoiceDate(), command.getReservationCode(), command.getCouponNumber());

        if(Objects.isNull(currentBookingDto)) {
            processed = processNewBooking(command, hotelDto);
        }else{
            processed = processExistingBooking(command, hotelDto, currentBookingDto);
        }

        updateLog(command.getId(), 1, processed ? 1 : 0);
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

    private boolean processNewBooking(CreateGroupedRatesCommand command, ManageHotelDto hotelDto){
        createBookingWithRates(hotelDto, command.getRoomRateCommandList());
        return true;
    }

    private boolean processExistingBooking(CreateGroupedRatesCommand command, ManageHotelDto hotelDto, BookingDto currentBooking){
        if(!currentBooking.getStatus().equals(BookingStatus.PROCESSED)) {
            List<RoomRateDto> currentRoomRateDtos = service.findByBooking(currentBooking.getId());
            if (shouldCancelBooking(currentRoomRateDtos, command.getRoomRateCommandList())) {
                cancelBookingAndRates(currentBooking, currentRoomRateDtos);
                createBookingWithRates(hotelDto, command.getRoomRateCommandList());
                return true;
            }

            if(!findRecordsWithDifferentHash(currentRoomRateDtos, command.getRoomRateCommandList()).isEmpty()){
                cancelBookingAndRates(currentBooking, currentRoomRateDtos);
                createBookingWithRates(hotelDto, command.getRoomRateCommandList());
                return true;
            }

            return false;
        }
        return false;
    }

    private void createBookingWithRates(ManageHotelDto hotelDto, List<CreateRoomRateCommand> rateCommands){
        BookingDto newBookingDto = buildBooking(hotelDto, rateCommands);
        bookingService.create(newBookingDto);
        addRatesToBooking(rateCommands, newBookingDto, hotelDto);
    }

    private void addRatesToBooking(List<CreateRoomRateCommand> rateCommands, BookingDto currentBooking, ManageHotelDto hotelDto){
        List<RoomRateDto> newRates = rateCommands.stream()
                .map(rateCommand -> buildRoomRateDto(rateCommand, currentBooking, hotelDto))
                .collect(Collectors.toList());
        service.createMany(newRates);
    }

    private boolean shouldCancelBooking(List<RoomRateDto> currentRates, List<CreateRoomRateCommand> newRates){
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

    private void cancelBookingAndRates(BookingDto currentBooking, List<RoomRateDto> currentRates){
        cancelBooking(currentBooking);
        cancelRates(currentRates);
    }

    private void cancelBooking(BookingDto bookingDto){
        bookingDto.setStatus(BookingStatus.DELETED);
        bookingDto.setUpdatedAt(LocalDateTime.now());
        bookingService.update(bookingDto);
    }

    private void cancelRates(List<RoomRateDto> ratesToCancell){
        ratesToCancell.stream().forEach(rate -> rate.setStatus(RoomRateStatus.DELETED));
        service.updateMany(ratesToCancell);
    }

    public static List<CreateRoomRateCommand> findRecordsWithDifferentHash(List<RoomRateDto> roomRateDtos, List<CreateRoomRateCommand> createRoomRateCommands) {
        Set<String> roomRateDtoHashes = roomRateDtos.stream()
                .map(RoomRateDto::getHash)
                .collect(Collectors.toSet());

        return createRoomRateCommands.stream()
                .filter(command -> !roomRateDtoHashes.contains(command.getHash()))
                .collect(Collectors.toList());
    }

    private BookingDto buildBooking(ManageHotelDto hotelDto, List<CreateRoomRateCommand> rateCommands){
        return new BookingDto(
                UUID.randomUUID(),
                BookingStatus.PENDING,
                hotelDto,
                null,
                rateCommands.get(0).getAgency(),
                manageAgencyService.findByCode(rateCommands.get(0).getAgency()),
                rateCommands.stream()
                        .map(CreateRoomRateCommand::getCheckInDate)
                        .max(Comparator.naturalOrder()).orElse(rateCommands.get(0).getCheckInDate()),
                rateCommands.stream()
                        .map(CreateRoomRateCommand::getCheckOutDate)
                        .max(Comparator.naturalOrder()).orElse(rateCommands.get(0).getCheckOutDate()),
                rateCommands.stream()
                        .mapToInt(CreateRoomRateCommand::getStayDays)
                        .sum(),
                rateCommands.get(0).getReservationCode(),
                rateCommands.get(0).getGuestName(),
                rateCommands.get(0).getFirstName(),
                rateCommands.get(0).getLastName(),
                rateCommands.stream()
                        .mapToDouble(CreateRoomRateCommand::getAmount)
                        .sum(),
                rateCommands.get(0).getRoomType(),
                manageRoomTypeService.findByCodeAndHotel(rateCommands.get(0).getRoomType(), hotelDto.getId()),
                rateCommands.get(0).getCouponNumber(),
                rateCommands.get(0).getTotalNumberOfGuest(),
                rateCommands.get(0).getAdults(),
                rateCommands.get(0).getChildrens(),
                rateCommands.get(0).getRatePlan(),
                manageRatePlanService.findByCodeAndHotel(rateCommands.get(0).getRatePlan(), hotelDto.getId()),
                rateCommands.get(0).getInvoicingDate(),
                rateCommands.get(0).getHotelCreationDate(),
                rateCommands.stream()
                        .mapToDouble(CreateRoomRateCommand::getOriginalAmount)
                        .sum(),
                rateCommands.stream()
                        .mapToDouble(CreateRoomRateCommand::getAmountPaymentApplied)
                        .sum(),
                rateCommands.get(0).getRateByAdult(),
                rateCommands.get(0).getRateByChild(),
                rateCommands.get(0).getRemarks(),
                rateCommands.get(0).getRoomNumber(),
                rateCommands.get(0).getHotelInvoiceAmount() == 0
                        || rateCommands.get(0).getQuote() == 0 ? 0
                        :Math.round((rateCommands.get(0).getHotelInvoiceAmount() / rateCommands.get(0).getQuote()) * 100.0) / 100.0,
                rateCommands.get(0).getHotelInvoiceNumber(),
                rateCommands.get(0).getInvoiceFolioNumber(),
                rateCommands.get(0).getQuote(),
                rateCommands.get(0).getRenewalNumber(),
                rateCommands.get(0).getRoomCategory(),
                roomCategoryService.findByCode(rateCommands.get(0).getRoomCategory()),
                rateCommands.get(0).getHash(),
                ""
        );
    }

    private RoomRateDto buildRoomRateDto(CreateRoomRateCommand createRoomRateCommand, BookingDto currentBooking, ManageHotelDto hotelDto){
        return new RoomRateDto(
                UUID.randomUUID(),
                createRoomRateCommand.getStatus(),
                hotelDto,
                null,
                createRoomRateCommand.getAgency(),
                createRoomRateCommand.getCheckInDate(),
                createRoomRateCommand.getCheckOutDate(),
                createRoomRateCommand.getStayDays(),
                createRoomRateCommand.getReservationCode(),
                createRoomRateCommand.getGuestName(),
                createRoomRateCommand.getFirstName(),
                createRoomRateCommand.getLastName(),
                createRoomRateCommand.getAmount(),
                createRoomRateCommand.getRoomType(),
                createRoomRateCommand.getCouponNumber(),
                createRoomRateCommand.getTotalNumberOfGuest(),
                createRoomRateCommand.getAdults(),
                createRoomRateCommand.getChildrens(),
                createRoomRateCommand.getRatePlan(),
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
                currentBooking
        );
    }
}
