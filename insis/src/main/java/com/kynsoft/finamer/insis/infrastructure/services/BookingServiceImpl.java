package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.booking.BookingResponse;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.repository.command.BookingWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.BookingReadDataJPARepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements IBookingService {

    private final BookingWriteDataJPARepository writeRepository;
    private final BookingReadDataJPARepository readRepository;

    public BookingServiceImpl(BookingWriteDataJPARepository writeRepository, BookingReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(BookingDto dto) {
        Booking booking = new Booking(dto);
        return writeRepository.save(booking).getId();
    }

    @Override
    public void update(BookingDto dto) {
        Booking booking = new Booking(dto);
        writeRepository.save(booking);
    }

    @Override
    public void updateMany(List<BookingDto> dtoList) {
        List<Booking> bookings = dtoList.stream()
                .map(Booking::new)
                .toList();

        writeRepository.saveAll(bookings);
    }

    @Transactional
    @Override
    public int updateAgencyByAgencyCode(ManageAgencyDto agencyDto) {
        ManageAgency agency = new ManageAgency(agencyDto);
        return writeRepository.updateAgencyByAgencyCodeAndStatus(agency, agency.getCode());
    }

    @Override
    public void delete(BookingDto dto) {
        try {
            writeRepository.deleteById(dto.getId());
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public BookingDto findById(UUID id) {
        Optional<Booking> booking = readRepository.findById(id);
        return booking.map(Booking::toAggregate)
                .orElse(null);
    }

    @Override
    public List<BookingDto> findAllByIds(List<UUID> idList) {
        return readRepository.findByIdIn(idList)
                .stream()
                .map(Booking::toAggregate)
                .toList();
    }

    @Override
    public BookingDto findByTcaId(ManageHotelDto hotelDto, LocalDate invoicingDate, String reservationNumber, String couponNumber) {
        ManageHotel hotel = new ManageHotel(hotelDto);
        Optional<Booking> booking = readRepository.findByHotelAndInvoicingDateAndReservationCodeAndCouponNumber(hotel,
                invoicingDate,
                reservationNumber,
                couponNumber);
        return booking.map(Booking::toAggregate)
                .orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Booking> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Booking> data = readRepository.findAll(specifications, pageable);

        return getPagintatedResponse(data);
    }

    public PaginatedResponse getPagintatedResponse(Page<Booking> data){
        List<BookingResponse> response = new ArrayList<>();
        for(Booking booking : data.getContent()){
            response.add(new BookingResponse(booking.toAggregate()));
        }
        return new PaginatedResponse(response, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}