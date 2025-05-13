package com.kynsoft.finamer.insis.domain.rules.booking;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.ImportBookingDto;
import com.kynsoft.finamer.insis.domain.services.IImportBookingService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ImportBookingSizeRuleOnFail implements RulesChecker.IBrokenRuleCallback {

    private final UUID importProcessId;
    private final List<BookingDto> bookingsToImport;
    private final List<BookingDto> bookingsAvailable;
    private final IImportBookingService importBookingService;

    public ImportBookingSizeRuleOnFail(UUID importProcessId,
                                       List<BookingDto> bookingsToImport,
                                       List<BookingDto> bookingsAvailable,
                                       IImportBookingService importBookingService){
        this.importProcessId = importProcessId;
        this.bookingsToImport = bookingsToImport;
        this.bookingsAvailable = bookingsAvailable;
        this.importBookingService = importBookingService;
    }

    @Override
    public void onFail() {
        List<BookingDto> duplicatedBookings = removeAllById(bookingsToImport, bookingsAvailable);
        List<ImportBookingDto> duplicatedImportBookings = importBookingService.findByImportProcessIdAndBookings(importProcessId, duplicatedBookings);

        duplicatedImportBookings.forEach(
                bookingWithError -> {
                    bookingWithError.setErrorMessage("The booking already exists in another import process");
                    bookingWithError.setUpdatedAt(LocalDateTime.now());
                }
        );
        importBookingService.updateMany(duplicatedImportBookings);
    }

    private List<BookingDto> removeAllById(List<BookingDto> booking, List<BookingDto> bookingToRemove){
        Set<UUID> bookingsToRemoveIds = bookingToRemove.stream()
                .map(BookingDto::getId)
                .collect(Collectors.toSet());

        return booking.stream()
                .filter(bookingDto -> !bookingsToRemoveIds.contains(bookingDto.getId()))
                .toList();
    }
}
