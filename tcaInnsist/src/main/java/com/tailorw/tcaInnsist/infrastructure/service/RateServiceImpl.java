package com.tailorw.tcaInnsist.infrastructure.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.tailorw.tcaInnsist.application.query.objectResponse.RateResponse;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.ManageRateDto;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.*;
import com.tailorw.tcaInnsist.domain.services.utils.IEncryptionService;
import com.tailorw.tcaInnsist.infrastructure.model.Rate;
import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageConnection;
import com.tailorw.tcaInnsist.infrastructure.repository.interfaces.IRateRepository;
import com.tailorw.tcaInnsist.infrastructure.utils.search.SearchUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class RateServiceImpl implements IRateService {

    private final IRateRepository manageRateRepository;
    private final IEncryptionService encryptionService;

    public RateServiceImpl(IRateRepository manageRateRepository, IEncryptionService encryptionService){
        this.manageRateRepository = manageRateRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public PaginatedResponse searchRatesByBookingId(Pageable pageable, ManageConnectionDto configurationDto, String reservationNumber, String cuponNumber, String checkIn, String checkOut) {
        ManageConnection configurationProperties = new ManageConnection(configurationDto);
        configurationProperties.setUserName(encryptionService.decrypt(configurationDto.getUserName()));
        configurationProperties.setPassword(encryptionService.decrypt(configurationDto.getPassword()));

        List<RateDto> rateDtos  = manageRateRepository.findAllByBookingId(configurationProperties, reservationNumber, cuponNumber, checkIn, checkOut)
                .stream()
                .map(Rate::toAggregate).collect(Collectors.toList());
        Page<RateResponse> data = convertListToPage(SearchUtil.convertToResponse(rateDtos, RateResponse::new), pageable);
        return getPaginatedResponse(data);
    }


    @Override
    public List<Rate> findByCriteria(ManageHotelDto hotelDto, ManageConnectionDto configurationDto, List<FilterCriteria> filterCriteria) {
        ManageConnection configurationProperties = new ManageConnection(configurationDto);
        configurationProperties.setUserName(encryptionService.decrypt(configurationDto.getUserName()));
        configurationProperties.setPassword(encryptionService.decrypt(configurationDto.getPassword()));

        return manageRateRepository.findAllByCriteria(configurationProperties, filterCriteria, hotelDto.getRoomType());
    }

    @Override
    public List<RateDto> findByInvoiceDate(ManageHotelDto hotelDto, ManageConnectionDto configurationDto, LocalDate invoiceDate) {
        ManageConnection configurationProperties = new ManageConnection(configurationDto);
        configurationProperties.setUserName(encryptionService.decrypt(configurationDto.getUserName()));
        configurationProperties.setPassword(encryptionService.decrypt(configurationDto.getPassword()));

        Logger.getLogger(RateServiceImpl.class.getName()).log(Level.INFO, String.format("ConnectionProperties. User: %s, Pass: %s Bdd: %s, IP: %s, Puerto: %s", configurationProperties.getUserName(), configurationProperties.getPassword(), configurationProperties.getDbName(), configurationProperties.getId(), configurationProperties.getPort()));

        List<RateDto> rateDtos = manageRateRepository.findByInvoiceCreatedAt(configurationProperties, invoiceDate, hotelDto.getRoomType()).stream()
                .map(Rate::toAggregate)
                .collect(Collectors.toList());

        rateDtos.forEach(rateDto -> rateDto.setHotelCode(hotelDto.getCode()));

        return rateDtos;
    }

    @Override
    public List<RateDto> findBetweenInvoiceDates(ManageHotelDto hotelDto, ManageConnectionDto configurationDto, LocalDate invoiceDateStart, LocalDate invoiceDateEnd) {
        ManageConnection configurationProperties = new ManageConnection(configurationDto);
        configurationProperties.setUserName(encryptionService.decrypt(configurationDto.getUserName()));
        configurationProperties.setPassword(encryptionService.decrypt(configurationDto.getPassword()));

        return manageRateRepository.findBetweenInvoiceDates(configurationProperties, invoiceDateStart, invoiceDateEnd, hotelDto.getRoomType())
                .stream()
                .map(Rate::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateRate(ManageRateDto newRate, ManageRateDto oldRate){
        return oldRate == null || !Objects.equals(oldRate.getHash(), newRate.getHash());
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

    private PaginatedResponse getPaginatedResponse(Page<RateResponse> data) {
        List<RateResponse> responseList = new ArrayList<>(data.getContent());
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
