package com.kynsoft.finamer.insis.application.command.roomRate.importRoomRate;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.rules.roomRate.ImportRoomRateSizeRule;
import com.kynsoft.finamer.insis.domain.services.*;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import com.kynsoft.finamer.insis.infrastructure.model.http.importBooking.ImportInnsistBookingRequest;
import com.kynsoft.finamer.insis.infrastructure.model.http.importBooking.ImportInnsistRequest;
import com.kynsoft.finamer.insis.infrastructure.model.http.importBooking.ImportInnsistRoomRateRequest;
import com.kynsoft.finamer.insis.infrastructure.services.http.ImportRoomRateHttpService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.booking.ProducerImportInnsistBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ImportRoomRateCommandHandler implements ICommandHandler<ImportRoomRateCommand> {

    private final IImportProcessService service;
    private final IManageEmployeeService employeeService;
    private final IImportRoomRateService importRoomRateService;
    private final IRoomRateService roomRateService;
    private final ProducerImportInnsistBookingService producerImportInnsistBookingService;

    private final ImportRoomRateHttpService importHttpService;

    public ImportRoomRateCommandHandler(IImportProcessService service,
                                        IManageEmployeeService employeeService,
                                        IImportRoomRateService importRoomRateService,
                                        IRoomRateService roomRateService,
                                        ProducerImportInnsistBookingService producerImportInnsistBookingService,
                                        ImportRoomRateHttpService importHttpService){
        this.service = service;
        this.employeeService = employeeService;
        this.importRoomRateService = importRoomRateService;
        this.roomRateService = roomRateService;
        this.producerImportInnsistBookingService = producerImportInnsistBookingService;
        this.importHttpService = importHttpService;
    }

    @Override
    public void handle(ImportRoomRateCommand command) {
        ManageEmployeeDto employee = getEmployee(command.getUserId());
        //List<BookingDto> bookingsToImport = getBookingsToImport(command.getBookings());
        List<RoomRateDto> availableRoomRates = getAvailableRoomRates(command.getRoomRates());

        ImportProcessDto importProcess = this.createImportProcess(command.getId(), command.getRoomRates().size(), employee.getId());
        this.saveImportRoomRates(importProcess, availableRoomRates);

        RulesChecker.checkRule(new ImportRoomRateSizeRule(command.roomRates.size(), availableRoomRates.size()));

        this.setRoomRatesInProcess(availableRoomRates);

        //Llamada a Kafka
        this.sendRoomRatesToProcess(importProcess, employee, availableRoomRates);
        //Llamada por HTTP
        //sendRoomRatesToHttpProcess(importProcess, employee, availableRoomRates);

        this.updateImportProcessStatus(importProcess, ImportProcessStatus.IN_PROCESS);
    }

    private ManageEmployeeDto getEmployee(UUID id){
        return employeeService.findById(id);
    }

    private ImportProcessDto createImportProcess(UUID processId, int roomRatesSize, UUID employeeId){
        ImportProcessDto dto = new ImportProcessDto(
                processId,
                ImportProcessStatus.CREATED,
                LocalDate.now(),
                null,
                roomRatesSize,
                employeeId,
                0,
                0
        );

        return service.create(dto);
    }

    private void updateImportProcessStatus(ImportProcessDto importProcess, ImportProcessStatus status){
        importProcess.setStatus(status);
        service.update(importProcess);
    }

    private List<RoomRateDto> getAvailableRoomRates(List<UUID> roomRatesIds){
        return roomRateService.findAllAvailableByIds(roomRatesIds);
    }

    /*
    private List<BookingDto> getBookingsToImport(List<UUID> bookingIds){
        return bookingService.findAllByIds(bookingIds);
    }*/

    private void saveImportRoomRates(ImportProcessDto importProcess, List<RoomRateDto> roomRates){
        List<ImportRoomRateDto> importRoomRateList = roomRates.stream()
                .map(roomRate -> {
                    return new ImportRoomRateDto(
                            UUID.randomUUID(),
                            importProcess,
                            roomRate
                    );
                }).toList();
        importRoomRateService.createMany(importRoomRateList);
    }

    private void setRoomRatesInProcess(List<RoomRateDto> roomRatesToImport){
        roomRatesToImport.forEach(roomRate -> {
            roomRate.setStatus(RoomRateStatus.IN_PROCESS);
            roomRate.setUpdatedAt(LocalDateTime.now());
        });
        this.roomRateService.updateMany(roomRatesToImport);
    }

    private void sendRoomRatesToProcess(ImportProcessDto importProcess, ManageEmployeeDto employee, List<RoomRateDto> roomRates){
        ImportInnsistKafka importInnsistKafka = new ImportInnsistKafka(
                importProcess.getId(),
                employee.getId().toString(),
                roomRates.stream().map(this::roomRatesToKafkaBooking).toList()
        );

        producerImportInnsistBookingService.create(importInnsistKafka);
    }

    private void sendRoomRatesToHttpProcess(ImportProcessDto importProcess, ManageEmployeeDto employee, List<RoomRateDto> roomRates){
        ImportInnsistRequest request = new ImportInnsistRequest(
                importProcess.getId(),
                employee.getId().toString(),
                roomRates.stream().map(this::roomRatesToBookingRequest).toList()
        );
        importInnsistHttp(request);
    }

    private ImportInnsistBookingKafka roomRatesToKafkaBooking(RoomRateDto roomRate){
        List<ImportInnsistRoomRateKafka> roomRateKafkaList = new ArrayList<>();
        roomRateKafkaList.add(roomRateToKafkaRoomRate(roomRate));

        return new ImportInnsistBookingKafka(
                roomRate.getId(),
                roomRate.getInvoicingDate().atStartOfDay(),
                roomRate.getHotelCreationDate().atStartOfDay(),
                roomRate.getInvoicingDate().atStartOfDay(),
                roomRate.getCheckInDate().atStartOfDay(),
                roomRate.getCheckOutDate().atStartOfDay(),
                roomRate.getReservationCode(),
                roomRate.getFirstName(),
                roomRate.getLastName(),
                roomRate.getRoomNumber(),
                roomRate.getAdults(),
                roomRate.getChildren(),
                roomRate.getStayDays(),
                roomRate.getRateByAdult(),
                roomRate.getRateByChild(),
                Long.parseLong(roomRate.getHotelInvoiceNumber().trim()),
                roomRate.getInvoiceFolioNumber(),
                roomRate.getHotelInvoiceAmount(),
                roomRate.getRemarks(),
                roomRate.getRatePlan().getCode(),
                null,
                roomRate.getRoomType().getCode(),
                roomRate.getRoomCategory().getCode(),
                roomRate.getHotel().getCode(),
                roomRate.getAgencyCode(),
                roomRate.getCouponNumber(),
                roomRateKafkaList
        );
    }

    private ImportInnsistRoomRateKafka roomRateToKafkaRoomRate(RoomRateDto roomRate){
        return new ImportInnsistRoomRateKafka(
                roomRate.getCheckInDate().atStartOfDay(),
                roomRate.getCheckOutDate().atStartOfDay(),
                roomRate.getAmount(),
                roomRate.getRoomNumber(),
                roomRate.getAdults(),
                roomRate.getChildren(),
                roomRate.getRateByAdult(),
                roomRate.getRateByChild(),
                roomRate.getHotelInvoiceAmount(),
                roomRate.getRemarks(),
                (long)roomRate.getStayDays()
        );
    }

    private ImportInnsistBookingRequest roomRatesToBookingRequest(RoomRateDto roomRate){
        List<ImportInnsistRoomRateRequest> roomRateKafkaList = new ArrayList<>();
        roomRateKafkaList.add(roomRateToRoomRateRequest(roomRate));

        return new ImportInnsistBookingRequest(
                roomRate.getId(),
                roomRate.getInvoicingDate().atStartOfDay(),
                roomRate.getHotelCreationDate().atStartOfDay(),
                roomRate.getInvoicingDate().atStartOfDay(),
                roomRate.getCheckInDate().atStartOfDay(),
                roomRate.getCheckOutDate().atStartOfDay(),
                roomRate.getReservationCode(),
                roomRate.getFirstName(),
                roomRate.getLastName(),
                roomRate.getRoomNumber(),
                roomRate.getAdults(),
                roomRate.getChildren(),
                roomRate.getStayDays(),
                roomRate.getRateByAdult(),
                roomRate.getRateByChild(),
                Long.parseLong(roomRate.getHotelInvoiceNumber().trim()),
                roomRate.getInvoiceFolioNumber(),
                roomRate.getHotelInvoiceAmount(),
                roomRate.getRemarks(),
                roomRate.getRatePlan().getCode(),
                null,
                roomRate.getRoomType().getCode(),
                roomRate.getRoomCategory().getCode(),
                roomRate.getHotel().getCode(),
                roomRate.getAgencyCode(),
                roomRate.getCouponNumber(),
                roomRateKafkaList,
                null,
                false
        );
    }

    private ImportInnsistRoomRateRequest roomRateToRoomRateRequest(RoomRateDto roomRate){
        return new ImportInnsistRoomRateRequest(
                roomRate.getCheckInDate().atStartOfDay(),
                roomRate.getCheckOutDate().atStartOfDay(),
                roomRate.getAmount(),
                roomRate.getRoomNumber(),
                roomRate.getAdults(),
                roomRate.getChildren(),
                roomRate.getRateByAdult(),
                roomRate.getRateByChild(),
                roomRate.getHotelInvoiceAmount(),
                roomRate.getRemarks(),
                (long)roomRate.getStayDays()
        );
    }

    private void importInnsistHttp(ImportInnsistRequest importInnsistRequest){
        importHttpService.sendRoomRatesToImport(importInnsistRequest);
    }
}
