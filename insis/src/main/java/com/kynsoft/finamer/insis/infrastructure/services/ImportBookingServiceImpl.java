package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.booking.BookingResponse;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.ImportBookingDto;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.services.IImportBookingService;
import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ImportBooking;
import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ImportBookingWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ImportBookingReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImportBookingServiceImpl implements IImportBookingService {

    private final ImportBookingWriteDataJPARepository writeRepository;
    private final ImportBookingReadDataJPARepository readRepository;

    public ImportBookingServiceImpl(ImportBookingWriteDataJPARepository writeRepository,
                                    ImportBookingReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ImportBookingDto dto) {
        ImportBooking importBooking = new ImportBooking(dto);
        return writeRepository.save(importBooking).getId();
    }

    @Override
    public List<ImportBookingDto> createMany(List<ImportBookingDto> dtoList) {
        List<ImportBooking> importBookingList = dtoList.stream()
                .map(ImportBooking::new)
                .toList();

        importBookingList = writeRepository.saveAll(importBookingList);

        return importBookingList.stream()
                .map(ImportBooking::toAggregate)
                .toList();
    }

    @Override
    public void update(ImportBookingDto dto) {
        ImportBooking importBooking = new ImportBooking(dto);
        writeRepository.save(importBooking);
    }

    @Override
    public void updateMany(List<ImportBookingDto> dtoList) {
        List<ImportBooking> importBookingList = dtoList.stream()
                .map(ImportBooking::new)
                .toList();

        writeRepository.saveAll(importBookingList);
    }

    @Override
    public List<ImportBookingDto> findByImportProcessId(UUID importProcessId) {
        return readRepository.findByImportProcess_Id(importProcessId).stream()
                .map(ImportBooking::toAggregate)
                .toList();
    }

    @Override
    public List<ImportBookingDto> findByImportProcessIdAndBookingId(UUID importProcessId, UUID bookingId) {
        return readRepository.findByImportProcess_IdAndBooking_Id(importProcessId, bookingId).stream()
                .map(ImportBooking::toAggregate)
                .toList();
    }

    @Override
    public List<ImportBookingDto> findByImportProcessIdAndBookings(UUID importProcessId, List<BookingDto> bookingDtoList) {
        List<Booking> bookings = bookingDtoList.stream().
                map(Booking::new).toList();

        return readRepository.findByImportProcess_IdAndBookingIn(importProcessId, bookings).stream()
                .map(ImportBooking::toAggregate)
                .toList();
    }

    @Override
    public PaginatedResponse getBookingErrorsByImportProcessId(UUID importProcessId, Pageable pageable) {
        List<ImportBookingDto> importBookingDtos = readRepository.findByImportProcess_Id(importProcessId)
                .stream()
                .filter(importBooking -> Objects.nonNull(importBooking.getErrorMessage()) && !importBooking.getErrorMessage().isEmpty())
                .map(ImportBooking::toAggregate).toList();

        Page<ImportBookingDto> page = convertListToPage(importBookingDtos, pageable);
        return getPaginatedResponse(page);
    }

    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<T> pageContent;

        if (list.size() < startItem) {
            pageContent = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            pageContent = list.subList(startItem, toIndex);
        }

        return new PageImpl<>(pageContent, pageable, list.size());
    }

    private PaginatedResponse getPaginatedResponse(Page<ImportBookingDto> data) {
        List<BookingResponse> responseList = data.getContent().stream()
                .map(importBooking -> {
                    BookingResponse bookingResponse = new BookingResponse(importBooking.getBooking());
                    bookingResponse.setMessage(importBooking.getErrorMessage());
                    return bookingResponse;
                })
                .toList();

        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
