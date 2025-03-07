package com.kynsoft.finamer.insis.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.ImportBookingDto;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IImportBookingService {

    UUID create(ImportBookingDto dto);

    List<ImportBookingDto> createMany(List<ImportBookingDto> dtoList);

    void update(ImportBookingDto dto);

    void updateMany(List<ImportBookingDto> dtoList);

    List<ImportBookingDto> findByImportProcessId(UUID importProcessId);

    List<ImportBookingDto> findByImportProcessIdAndBookingId(UUID importProcessId, UUID bookingId);

    List<ImportBookingDto> findByImportProcessIdAndBookings(UUID importProcessId, List<BookingDto> bookingDtoList);

    PaginatedResponse getBookingErrorsByImportProcessId(UUID importProcessId, Pageable pageable);
}
