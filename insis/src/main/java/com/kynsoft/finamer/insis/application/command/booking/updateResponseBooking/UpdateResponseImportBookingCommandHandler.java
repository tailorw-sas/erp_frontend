package com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.ImportBookingDto;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.domain.services.IImportBookingService;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UpdateResponseImportBookingCommandHandler implements ICommandHandler<UpdateResponseImportBookingCommand> {

    private final IBookingService bookingService;
    private final IImportProcessService importProcessService;
    private final IImportBookingService importBookingService;

    public UpdateResponseImportBookingCommandHandler(IBookingService bookingService,
                                                     IImportProcessService importProcessService,
                                                     IImportBookingService importBookingService){
        this.bookingService = bookingService;
        this.importProcessService = importProcessService;
        this.importBookingService = importBookingService;
    }

    @Override
    public void handle(UpdateResponseImportBookingCommand command) {
        ImportProcessDto importProcess = getImportProcess(command.getImportProcessId());
        if(command.getErrorResponses().isEmpty()){
            saveBookingsResponseWhenSuccessfull(importProcess.getId());
        }else{
            saveBookingsResponseWhenFailed(importProcess.getId(), command.getErrorResponses());
        }

        int totalFailed = getTotalFailed(command.getImportProcessId());
        updateImportProcessStatus(importProcess, ImportProcessStatus.COMPLETED, totalFailed);
    }

    private ImportProcessDto getImportProcess(UUID id){
        return importProcessService.findById(id);
    }

    private void saveBookingsResponseWhenSuccessfull(UUID importProcessId){
        List<ImportBookingDto> importBookings = importBookingService.findByImportProcessId(importProcessId);

        importBookings.forEach(importBookingDto -> {
            importBookingDto.setUpdatedAt(LocalDateTime.now());
        });

        importBookingService.updateMany(importBookings);

        List<BookingDto> bookings = importBookings.stream()
                .map(ImportBookingDto::getBooking)
                .toList();

        updateBookingsStatus(bookings, BookingStatus.PROCESSED);
    }

    private void saveBookingsResponseWhenFailed(UUID importProcessId, List<ErrorResponse> errorResponses) {
        List<ImportBookingDto> importBookings = importBookingService.findByImportProcessId(importProcessId);

        Map<UUID, String> errorMap = errorResponses.stream()
                .collect(Collectors.toMap(ErrorResponse::getBookingId, ErrorResponse::getErrorMessage));

        importBookings.forEach(importBooking -> {
            UUID bookingId = importBooking.getBooking().getId();
            if (errorMap.containsKey(bookingId)) {
                importBooking.setErrorMessage(errorMap.get(bookingId));
            }
            importBooking.setUpdatedAt(LocalDateTime.now());
        });

        importBookingService.updateMany(importBookings);

        List<BookingDto> bookingsImported = importBookings.stream()
                .map(ImportBookingDto::getBooking)
                .filter(booking -> !errorMap.containsKey(booking.getId()))
                .toList();

        updateBookingsStatus(bookingsImported, BookingStatus.PENDING);

        List<BookingDto> bookingsWithErrors = importBookings.stream()
                .map(ImportBookingDto::getBooking)
                .filter(booking -> errorMap.containsKey(booking.getId()))
                .toList();

        updateBookingsStatus(bookingsWithErrors, BookingStatus.FAILED);
    }

    private void updateBookingsStatus(List<BookingDto> bookings, BookingStatus status){
        bookings.forEach(booking -> {
            booking.setStatus(status);
            booking.setUpdatedAt(LocalDateTime.now());
        });

        bookingService.updateMany(bookings);
    }

    private void updateImportProcessStatus(ImportProcessDto importProcess, ImportProcessStatus status, int totalFailed){
        importProcess.setStatus(status);
        importProcess.setCompletedAt(LocalDateTime.now());
        importProcess.setTotalSuccessful(importProcess.getTotalBookings() - totalFailed);
        importProcess.setTotalFailed(totalFailed);
        importProcessService.update(importProcess);
    }

    private int getTotalFailed(UUID processId){
        List<ImportBookingDto> bookings = importBookingService.findByImportProcessId(processId);
        return bookings.stream()
                .filter(importBookingDto -> Objects.nonNull(importBookingDto.getErrorMessage()))
                .toList().size();
    }
}