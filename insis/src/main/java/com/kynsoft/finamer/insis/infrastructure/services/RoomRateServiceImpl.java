package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.roomRate.RoomRateResponse;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import com.kynsoft.finamer.insis.infrastructure.repository.command.RoomRateWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ImportRoomRateReadDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.RoomRateReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RoomRateServiceImpl implements IRoomRateService {

    private final RoomRateWriteDataJPARepository writeRepository;
    private final RoomRateReadDataJPARepository readRepository;
    private final ImportRoomRateReadDataJPARepository importRoomRateRepository;

    public RoomRateServiceImpl(RoomRateWriteDataJPARepository writeRepository,
                               RoomRateReadDataJPARepository readRepository,
                               ImportRoomRateReadDataJPARepository importRoomRateRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
        this.importRoomRateRepository = importRoomRateRepository;
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
        if(Objects.nonNull(id)){
            Optional<RoomRate> rate = readRepository.findById(id);
            return rate.map(RoomRate::toAggregate).orElse(null);
        }
        throw new IllegalArgumentException("ID must be provided and cannot be null");
    }

    @Override
    public RoomRateDto findByTcaId(RoomRateDto dto) {
        if(Objects.nonNull(dto)){
            Optional<RoomRate> rate = readRepository.findByHotelAndInvoicingDateAndReservationCodeAndCouponNumberAndRenewalNumber(new ManageHotel(dto.getHotel()) ,
                    dto.getInvoicingDate(),
                    dto.getReservationCode(),
                    dto.getCouponNumber(),
                    dto.getRenewalNumber());
            return rate.map(RoomRate::toAggregate).orElse(null);
        }
        throw new IllegalArgumentException("RoomRateDto must be provided and cannot be null.");
    }

    @Override
    public List<RoomRateDto> findByHotelAndInvoiceDate(UUID hotelId, LocalDate invoiceDate) {
        if(Objects.nonNull(hotelId) && Objects.nonNull(invoiceDate)){
            return readRepository.findByHotel_IdAndInvoicingDateAndStatusNot(hotelId, invoiceDate, RoomRateStatus.DELETED).stream()
                    .map(RoomRate::toAggregate).toList();
        }
        throw new IllegalArgumentException("Both hotelId and invoiceDate must be provided and cannot be null.");
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<RoomRate> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<RoomRate> data = readRepository.findAll(specifications, pageable);

        return getPagintatedResponse(data);
    }

    @Override
    public List<RoomRateDto> findAllAvailableByIds(List<UUID> idList) {
        if(Objects.nonNull(idList)){
            return readRepository.findByIdsAndStatuses(idList, List.of(RoomRateStatus.PENDING, RoomRateStatus.FAILED))
                    .stream()
                    .map(RoomRate::toAggregate)
                    .toList();
        }
        throw new IllegalArgumentException("RoomRate id list must not be null");
    }

    @Override
    public List<RoomRateDto> findAllByInvoiceId(UUID invoiceId) {
        if(Objects.nonNull(invoiceId)){
            return readRepository.findByInvoiceId(invoiceId).stream()
                    .map(RoomRate::toAggregate)
                    .toList();
        }
        throw new IllegalArgumentException("The invoiceId must not be null.");
    }

    public PaginatedResponse getPagintatedResponse(Page<RoomRate> data){
        List<RoomRateResponse> response = new ArrayList<>();
        for(RoomRate roomRate : data.getContent()){
            RoomRateResponse roomRateResponse = getRoomRateResponse(roomRate);
            response.add(roomRateResponse);
        }
        return new PaginatedResponse(response, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private RoomRateResponse getRoomRateResponse(RoomRate roomRate){
        RoomRateResponse roomRateResponse = new RoomRateResponse(roomRate.toAggregate());
        if(roomRate.getStatus().equals(RoomRateStatus.FAILED)){
            StringBuilder messageErrors = new StringBuilder();
            for(String message : getRoomRateErrorsHistory(roomRate.getId())){
                messageErrors.append(message).append("\n");
            }
            roomRateResponse.setMessage(messageErrors.toString());
        }
        return roomRateResponse;
    }

    private List<String> getRoomRateErrorsHistory(UUID roomRateId){
        return importRoomRateRepository.findByRoomRate_Id(roomRateId).stream()
                .filter(importRoomRate -> importRoomRate.getErrorMessage() != null && !importRoomRate.getErrorMessage().isBlank())
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .map(importRoomRate -> {
                    return importRoomRate.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ":" + importRoomRate.getErrorMessage();
                })
                .limit(2)
                .toList();
    }
}
