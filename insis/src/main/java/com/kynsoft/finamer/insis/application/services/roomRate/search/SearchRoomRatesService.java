package com.kynsoft.finamer.insis.application.services.roomRate.search;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsof.share.utils.DateConvert;
import com.kynsoft.finamer.insis.application.services.helpers.InvoiceDateRange;
import com.kynsoft.finamer.insis.application.services.roomRate.create.CreateRoomRateRequest;
import com.kynsoft.finamer.insis.application.services.roomRate.create.CreateRoomRatesService;
import com.kynsoft.finamer.insis.application.services.roomRate.externalSearch.SearchExternalRoomRatesService;
import com.kynsoft.finamer.insis.domain.dto.ExternalRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SearchRoomRatesService {

    private final IRoomRateService roomRateService;
    private final SearchExternalRoomRatesService searchExternalRoomRatesService;
    private final IManageHotelService manageHotelService;
    private final CreateRoomRatesService createRoomRatesService;

    public SearchRoomRatesService(IRoomRateService roomRateService,
                                  SearchExternalRoomRatesService searchExternalRoomRatesService,
                                  IManageHotelService manageHotelService,
                                  CreateRoomRatesService createRoomRatesService){
        this.roomRateService = roomRateService;
        this.searchExternalRoomRatesService = searchExternalRoomRatesService;
        this.manageHotelService = manageHotelService;
        this.createRoomRatesService = createRoomRatesService;
    }

    public PaginatedResponse search(Pageable pageable,
                                    List<FilterCriteria> filter,
                                    String query){
        Instant before = Instant.now();
        //PaginatedResponse response = this.searchRoomRates(pageable, filter);
        Instant after = Instant.now();
        System.out.println("*********** Search de room rates: " + Duration.between(before, after).toMillis());

        //if(response.getData().isEmpty()){
        try{
            InvoiceDateRange invoiceDateRange = this.extractInvoiceDateFilter(filter);
            ManageHotelDto hotelDto = this.getManageHotel(invoiceDateRange.getHotel());

            if(isRangeEqual(invoiceDateRange)){
                before = Instant.now();
                List<ExternalRoomRateDto> externalRoomRateDtos = this.searchExternalRoomRatesService.getExternalRoomRates(invoiceDateRange.getFrom(), hotelDto);
                after = Instant.now();
                System.out.println("*********** External Search de room rates: " + Duration.between(before, after).toMillis());

                before = Instant.now();
                this.createImportedRoomRates(hotelDto, invoiceDateRange.getFrom(), externalRoomRateDtos);
                after = Instant.now();
                System.out.println("*********** Create room rates: " + Duration.between(before, after).toMillis());
            }else{
                Map<LocalDate, List<ExternalRoomRateDto>> externalRoomRateMap = this.searchExternalRoomRatesService.getExternalRoomRates(hotelDto,
                        invoiceDateRange.getFrom(),
                        invoiceDateRange.getTo());
                for(Map.Entry<LocalDate, List<ExternalRoomRateDto>> rateGroup: externalRoomRateMap.entrySet()){
                    this.createImportedRoomRates(hotelDto, rateGroup.getKey(), rateGroup.getValue());
                }
            }

            return this.searchRoomRates(pageable, filter);
        }catch (Exception ex){
            return this.searchRoomRates(pageable, filter);
        }

        //}

        //return response;
    }

    public boolean isRangeEqual(InvoiceDateRange invoiceDateRange) {
        return invoiceDateRange.getFrom().equals(invoiceDateRange.getTo());
    }


    private PaginatedResponse searchRoomRates(Pageable pageable,
                                              List<FilterCriteria> filter){
        return roomRateService.search(pageable, filter);
    }

    public InvoiceDateRange extractInvoiceDateFilter(List<FilterCriteria> filters) {
        LocalDate from = null;
        LocalDate to = null;
        String hotel = "";

        for (FilterCriteria filterCriteria : filters) {
            if ("invoicingDate".equals(filterCriteria.getKey())) {
                if (filterCriteria.getOperator() == SearchOperation.GREATER_THAN_OR_EQUAL_TO){
                    from = DateConvert.convertStringToLocalDate(filterCriteria.getValue().toString(), DateConvert.getIsoLocalDateFormatter());
                }

                if (filterCriteria.getOperator() == SearchOperation.LESS_THAN_OR_EQUAL_TO){
                    to = DateConvert.convertStringToLocalDate(filterCriteria.getValue().toString(), DateConvert.getIsoLocalDateFormatter());
                }
            }
            if("hotel.id".equals(filterCriteria.getKey())){
                hotel = filterCriteria.getValue().toString();
            }
        }

        return new InvoiceDateRange(from, to, hotel);
    }

    private ManageHotelDto getManageHotel(String hotelCode){
        if(hotelCode.isEmpty()){
            throw new IllegalArgumentException("The hotel code must not be empty");
        }

        return this.manageHotelService.findById(UUID.fromString(hotelCode));
    }

    private void createImportedRoomRates(ManageHotelDto hotel, LocalDate invoiceDate, List<ExternalRoomRateDto> externalRoomRateDtos){
        UUID processId = UUID.randomUUID();
        List<CreateRoomRateRequest> createRoomRateRequests = externalRoomRateDtos.stream()
                .map(this::convertToRoomRateRequest)
                .collect(Collectors.toList());

        this.createRoomRatesService.createRoomRates(processId, hotel, invoiceDate, BatchType.MANUAL, createRoomRateRequests);
    }

    private CreateRoomRateRequest convertToRoomRateRequest(ExternalRoomRateDto externalRoomRate){
        return new CreateRoomRateRequest(
                UUID.randomUUID(),
                externalRoomRate.getHotelCode(),
                null,
                externalRoomRate.getAgencyCode(),
                externalRoomRate.getCheckInDate(),
                externalRoomRate.getCheckOutDate(),
                externalRoomRate.getStayDays(),
                externalRoomRate.getReservationCode(),
                externalRoomRate.getGuestName(),
                externalRoomRate.getFirstName(),
                externalRoomRate.getLastName(),
                externalRoomRate.getAmount(),
                externalRoomRate.getRoomTypeCode(),
                externalRoomRate.getCouponNumber(),
                externalRoomRate.getTotalNumberOfGuest(),
                externalRoomRate.getAdults(),
                externalRoomRate.getChildren(),
                externalRoomRate.getRatePlanCode(),
                externalRoomRate.getInvoicingDate(),
                externalRoomRate.getHotelCreationDate(),
                externalRoomRate.getOriginalAmount(),
                externalRoomRate.getAmountPaymentApplied(),
                externalRoomRate.getRateByAdult(),
                externalRoomRate.getRateByChild(),
                externalRoomRate.getRemarks(),
                externalRoomRate.getRoomNumber(),
                externalRoomRate.getHotelInvoiceAmount(),
                externalRoomRate.getHotelInvoiceNumber(),
                externalRoomRate.getInvoiceFolioNumber(),
                externalRoomRate.getQuote(),
                externalRoomRate.getRenewalNumber(),
                externalRoomRate.getRoomCategory(),
                externalRoomRate.getHash()
        );
    }
}
