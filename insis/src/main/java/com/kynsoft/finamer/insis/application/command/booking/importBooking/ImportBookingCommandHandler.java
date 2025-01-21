package com.kynsoft.finamer.insis.application.command.booking.importBooking;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.insis.domain.dto.*;
import com.kynsoft.finamer.insis.domain.services.*;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.booking.ProducerImportInnsistBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class ImportBookingCommandHandler implements ICommandHandler<ImportBookingCommand> {

    private final IImportProcessService service;
    private final IBookingService bookingService;
    private final IManageEmployeeService employeeService;
    private final IImportBookingService importBookingService;
    private final IRoomRateService roomRateService;
    private final ProducerImportInnsistBookingService producerImportInnsistBookingService;

    public ImportBookingCommandHandler(IImportProcessService service,
                                       IBookingService bookingService,
                                       IManageEmployeeService employeeService,
                                       IImportBookingService importBookingService,
                                       IRoomRateService roomRateService,
                                       ProducerImportInnsistBookingService producerImportInnsistBookingService){
        this.service = service;
        this.bookingService = bookingService;
        this.employeeService = employeeService;
        this.importBookingService = importBookingService;
        this.roomRateService = roomRateService;
        this.producerImportInnsistBookingService = producerImportInnsistBookingService;
    }

    @Override
    public void handle(ImportBookingCommand command) {
        ManageEmployeeDto employee = getEmployee(command.getUserId());
        List<BookingDto> bookings = getBookings(command.getBookings());

        ImportProcessDto importProcess = createImportProcess(command.getId(), bookings.size(), employee.getId(), 0, 0);
        saveImportBookings(importProcess, bookings);

        setBookingsInProcess(bookings);
        sendBookingToProcess(importProcess, employee, bookings);

        updateImportProcessStatus(importProcess, ImportProcessStatus.IN_PROCESS);
    }

    private ManageEmployeeDto getEmployee(UUID id){
        return employeeService.findById(id);
    }

    private ImportProcessDto createImportProcess(UUID processId, int bookingsSize, UUID employeeId, int totalSucessful, int totalFailed){
        ImportProcessDto dto = new ImportProcessDto(
                processId,
                ImportProcessStatus.CREATED,
                LocalDate.now(),
                null,
                bookingsSize,
                employeeId,
                totalSucessful,
                totalFailed
        );

        return service.create(dto);
    }

    private void updateImportProcessStatus(ImportProcessDto importProcess, ImportProcessStatus status){
        importProcess.setStatus(status);
        service.update(importProcess);
    }

    private List<BookingDto> getBookings(List<UUID> bookingIds){
        return bookingService.findAllByIds(bookingIds);
    }

    private void saveImportBookings(ImportProcessDto importProcess, List<BookingDto> bookings){
        List<ImportBookingDto> importBookingDtoList = bookings.stream()
                .map(importBooking -> {
                    return new ImportBookingDto(
                            UUID.randomUUID(),
                            importProcess,
                            importBooking
                    );
                }).toList();
        importBookingService.createMany(importBookingDtoList);
    }

    private void setBookingsInProcess(List<BookingDto> bookingsToImport){
        bookingsToImport.forEach(booking -> {
            booking.setStatus(BookingStatus.IN_PROCESS);
            booking.setUpdatedAt(LocalDateTime.now());
        });
        bookingService.updateMany(bookingsToImport);
    }

    private void sendBookingToProcess(ImportProcessDto importProcess, ManageEmployeeDto employee, List<BookingDto> bookings){
        ImportInnsistKafka importInnsistKafka = new ImportInnsistKafka(
                importProcess.getId(),
                employee.getFirstName() + " " +employee.getLastName(),
                bookings.stream().map(bookingKafka -> {
                    List<RoomRateDto> roomRateDtos = roomRateService.findByBooking(bookingKafka.getId());
                    return bookingToKafkaBooking(bookingKafka, roomRateDtos);
                }).toList()
        );

        producerImportInnsistBookingService.create(importInnsistKafka);
    }

    private ImportInnsistBookingKafka bookingToKafkaBooking(BookingDto booking, List<RoomRateDto> roomRates){
        return new ImportInnsistBookingKafka(
                booking.getId(),
                booking.getInvoicingDate().atStartOfDay(),
                booking.getHotelCreationDate().atStartOfDay(),
                booking.getInvoicingDate().atStartOfDay(),
                booking.getCheckInDate().atStartOfDay(),
                booking.getCheckOutDate().atStartOfDay(),
                booking.getReservationCode(), //booking.getHotelInvoiceNumber(),
                booking.getFirstName(),
                booking.getLastName(),
                booking.getRoomNumber(),
                booking.getAdults(),
                booking.getChildrens(),
                booking.getStayDays(),
                booking.getRateByAdult(),
                booking.getRateByChild(),
                Long.parseLong(booking.getHotelInvoiceNumber().trim()),
                booking.getInvoiceFolioNumber(),
                booking.getHotelInvoiceAmount(),
                booking.getRemarks(),
                booking.getRatePlan().getCode(),
                null,
                booking.getRoomType().getCode(),
                booking.getRoomCategory().getCode(),
                booking.getHotel().getCode(),
                booking.getAgencyCode(),//booking.getAgency().getCode(),
                booking.getCouponNumber(),
                roomRates.stream().map(this::roomRateToKafkaRoomRate).toList()
        );
    }

    private ImportInnsistRoomRateKafka roomRateToKafkaRoomRate(RoomRateDto roomRate){
        return new ImportInnsistRoomRateKafka(
                roomRate.getCheckInDate().atStartOfDay(),
                roomRate.getCheckOutDate().atStartOfDay(),
                roomRate.getAmount(),
                roomRate.getRoomNumber(),
                roomRate.getAdults(),
                roomRate.getChildrens(),
                roomRate.getRateByAdult(),
                roomRate.getRateByChild(),
                roomRate.getHotelInvoiceAmount(),
                roomRate.getRemarks(),
                (long)roomRate.getStayDays()
        );
    }
}
