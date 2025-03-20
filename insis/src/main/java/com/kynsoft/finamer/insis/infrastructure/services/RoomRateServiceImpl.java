package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.booking.BookingResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.roomRate.RoomRateResponse;
import com.kynsoft.finamer.insis.domain.dto.CountRoomRateByHotelAndInvoiceDateDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.Booking;
import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import com.kynsoft.finamer.insis.infrastructure.repository.command.RoomRateWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.RoomRateReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RoomRateServiceImpl implements IRoomRateService {

    private final RoomRateWriteDataJPARepository writeRepository;
    private final RoomRateReadDataJPARepository readRepository;

    public RoomRateServiceImpl(RoomRateWriteDataJPARepository writeRepository, RoomRateReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }


    @Override
    public UUID create(RoomRateDto dto) {
        RoomRate rate = new RoomRate(dto);
        return writeRepository.save(rate).getId();
    }

    @Override
    public void createMany(List<RoomRateDto> rateDtoList) {
        List<RoomRate> rateList = rateDtoList.stream()
                .map(RoomRate::new)
                .collect(Collectors.toList());
        writeRepository.saveAll(rateList);
    }

    @Override
    public void update(RoomRateDto dto) {
        RoomRate rate = new RoomRate(dto);
        rate.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(rate);
    }

    @Override
    public void updateMany(List<RoomRateDto> rateDtoList) {
        List<RoomRate> rateList = rateDtoList.stream()
                .map(RoomRate::new)
                .collect(Collectors.toList());
        writeRepository.saveAll(rateList);
    }

    @Override
    public void delete(RoomRateDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        } catch (Exception ex) {
            Logger.getLogger(RoomRateServiceImpl.class.getName()).log(Level.INFO, null, ex);
        }
    }

    @Override
    public RoomRateDto findById(UUID id) {
        Optional<RoomRate> rate = readRepository.findById(id);
        return rate.map(RoomRate::toAggregate).orElse(null);
    }

    @Override
    public RoomRateDto findByTcaId(RoomRateDto dto) {
        Optional<RoomRate> rate = readRepository.findByHotelAndInvoicingDateAndReservationCodeAndCouponNumberAndRenewalNumber(new ManageHotel(dto.getHotel()) ,
                dto.getInvoicingDate(),
                dto.getReservationCode(),
                dto.getCouponNumber(),
                dto.getRenewalNumber());
        return rate.map(RoomRate::toAggregate).orElse(null);

    }

    @Override
    public List<RoomRateDto> findByBooking(UUID bookingId) {
        return readRepository.findByBooking_Id(bookingId).stream()
                .map(RoomRate::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<CountRoomRateByHotelAndInvoiceDateDto> countByHotelsAndInvoiceDate(List<UUID> hotelIds, LocalDate fromInvoiceDate, LocalDate toInvoiceDate) {
        List<Object[]> resultados = readRepository.countByHotelAndInvoiceDate(hotelIds.toArray(new UUID[0]), fromInvoiceDate, toInvoiceDate);

        return readRepository.countByHotelAndInvoiceDate(hotelIds.toArray(new UUID[0]), fromInvoiceDate, toInvoiceDate).stream()
                .map(item -> new CountRoomRateByHotelAndInvoiceDateDto(
                        new ManageHotel(UUID.fromString((String)item[0]),
                                (String)item[1],
                                (String)item[2],
                                (String)item[3]).toAggregate(),
                        ((Date) item[4]).toLocalDate(),
                        (Long) item[5]))
                .toList();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<RoomRate> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<RoomRate> data = readRepository.findAll(specifications, pageable);

        return getPagintatedResponse(data);
    }

    public PaginatedResponse getPagintatedResponse(Page<RoomRate> data){
        List<RoomRateResponse> response = new ArrayList<>();
        for(RoomRate roomRate : data.getContent()){
            response.add(new RoomRateResponse(roomRate.toAggregate()));
        }
        return new PaginatedResponse(response, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
