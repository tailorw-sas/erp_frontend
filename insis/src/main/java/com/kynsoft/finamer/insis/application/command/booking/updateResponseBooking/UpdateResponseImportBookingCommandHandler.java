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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
        saveBookingsResponse(importProcess.getId(), command.getErrorResponses());
        updateImportProcessStatus(importProcess, ImportProcessStatus.COMPLETED);
    }

    private ImportProcessDto getImportProcess(UUID id){
        return importProcessService.findById(id);
    }

    private void saveBookingsResponse(UUID importProcessId, List<ErrorResponse> errorResponses) {
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

        List<UUID> bookingsImported = importBookings.stream()
                .filter(importBooking -> !errorMap.containsKey(importBooking.getBooking().getId()))
                .map(importBooking -> { return importBooking.getBooking().getId();})
                .toList();

        updateBookingsStatus(bookingsImported, BookingStatus.PROCESSED);

        List<UUID> bookingsWithErrors = importBookings.stream()
                .filter(importBooking -> errorMap.containsKey(importBooking.getBooking().getId()))
                .map(importBooking -> { return importBooking.getBooking().getId();})
                .toList();

        updateBookingsStatus(bookingsWithErrors, BookingStatus.FAILED);

        /*
        Map<Boolean, List<UUID>> partitionedBookings = errorResponses.stream()
                .collect(Collectors.partitioningBy(
                        error -> Objects.isNull(error.getErrorMessage()) || error.getErrorMessage().isEmpty(),
                        Collectors.mapping(ErrorResponse::getBookingId, Collectors.toList())
                ));

        updateBookingsStatus(partitionedBookings.get(true), BookingStatus.PROCESSED);
        updateBookingsStatus(partitionedBookings.get(false), BookingStatus.FAILED);
         */
    }

    private void updateBookingsStatus(List<UUID> idBookings, BookingStatus status){
        List<BookingDto> bookings = bookingService.findAllByIds(idBookings);
        bookings.forEach(booking -> {
                booking.setStatus(status);
                booking.setUpdatedAt(LocalDateTime.now());
        });

        bookingService.updateMany(bookings);
    }

    private void updateImportProcessStatus(ImportProcessDto importProcess, ImportProcessStatus status){
        importProcess.setStatus(status);
        importProcess.setCompletedAt(LocalDateTime.now());
        importProcessService.update(importProcess);
    }
}
