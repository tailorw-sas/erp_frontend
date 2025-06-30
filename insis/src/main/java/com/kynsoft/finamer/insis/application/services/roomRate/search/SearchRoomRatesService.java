package com.kynsoft.finamer.insis.application.services.roomRate.search;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsof.share.utils.DateConvert;
import com.kynsoft.finamer.insis.application.services.helpers.InvoiceDateRange;
import com.kynsoft.finamer.insis.application.services.roomRate.externalSearch.SearchExternalRoomRatesService;
import com.kynsoft.finamer.insis.domain.dto.ExternalRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchRoomRatesService {

    private final IRoomRateService roomRateService;
    private final SearchExternalRoomRatesService searchExternalRoomRatesService;
    private final IManageHotelService manageHotelService;

    public SearchRoomRatesService(IRoomRateService roomRateService,
                                  SearchExternalRoomRatesService searchExternalRoomRatesService,
                                  IManageHotelService manageHotelService){
        this.roomRateService = roomRateService;
        this.searchExternalRoomRatesService = searchExternalRoomRatesService;
        this.manageHotelService = manageHotelService;
    }

    public PaginatedResponse search(Pageable pageable,
                                    List<FilterCriteria> filter,
                                    String query){
        PaginatedResponse response = this.searchRoomRates(pageable, filter);

        if(response.getData().isEmpty()){
            InvoiceDateRange invoiceDateRange = this.extractInvoiceDateFilter(filter);
            //TODO Implementar una validacion de que si hay algunos dias en el rango de fechas lance un error de que solo se puede sincronizar un dia

            ManageHotelDto hotelDto = this.getManageHotel(invoiceDateRange.getHotel());
            List<ExternalRoomRateDto> externalRoomRateDtos = this.searchExternalRoomRatesService.getExternalRoomRates(invoiceDateRange.getFrom(), hotelDto);

        }

        return response;
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

        return this.manageHotelService.findByCode(hotelCode);
    }
}
