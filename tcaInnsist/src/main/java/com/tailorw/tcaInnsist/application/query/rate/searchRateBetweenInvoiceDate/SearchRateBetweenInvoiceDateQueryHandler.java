package com.tailorw.tcaInnsist.application.query.rate.searchRateBetweenInvoiceDate;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.utils.DateConvert;
import com.tailorw.tcaInnsist.application.query.objectResponse.RateByInvoiceDateResponse;
import com.tailorw.tcaInnsist.application.query.objectResponse.RateResponse;
import com.tailorw.tcaInnsist.application.query.objectResponse.SearchRateBetweenInvoiceDateResponse;
import com.tailorw.tcaInnsist.application.service.configuration.SearchConfigurationService;
import com.tailorw.tcaInnsist.application.service.manageHotel.SearchManageHotelService;
import com.tailorw.tcaInnsist.application.service.manageTradingCompany.SearchManageTradingCompanyService;
import com.tailorw.tcaInnsist.application.service.rate.search.SearchRateBetweenInvoiceDateService;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import com.tailorw.tcaInnsist.domain.rules.StringToDateFormatRule;
import com.tailorw.tcaInnsist.domain.services.IRateService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SearchRateBetweenInvoiceDateQueryHandler implements IQueryHandler<SearchRateBetweenInvoiceDateQuery, SearchRateBetweenInvoiceDateResponse> {

    private final SearchManageHotelService searchManageHotelService;
    private final SearchManageTradingCompanyService searchManageTradingCompanyService;
    private final SearchConfigurationService searchConfigurationService;
    private final SearchRateBetweenInvoiceDateService searchRateBetweenInvoiceDateService;

    public SearchRateBetweenInvoiceDateQueryHandler(IRateService rateService,
                                                    SearchManageHotelService searchManageHotelService,
                                                    SearchManageTradingCompanyService searchManageTradingCompanyService,
                                                    SearchConfigurationService searchConfigurationService,
                                                    SearchRateBetweenInvoiceDateService searchRateBetweenInvoiceDateService){
        this.searchManageHotelService = searchManageHotelService;
        this.searchManageTradingCompanyService = searchManageTradingCompanyService;
        this.searchConfigurationService = searchConfigurationService;
        this.searchRateBetweenInvoiceDateService = searchRateBetweenInvoiceDateService;
    }

    @Override
    public SearchRateBetweenInvoiceDateResponse handle(SearchRateBetweenInvoiceDateQuery query) {
        ManageHotelDto hotelDto = this.getManageHotelFromCode(query.getHotel());
        ManageTradingCompanyDto tradingCompanyDto = this.getManageTradingCompanyFromHotel(hotelDto);
        ManageConnectionDto connectionDto = this.getConnectionFromTradingCompany(tradingCompanyDto);

        RulesChecker.checkRule(new StringToDateFormatRule(query.getFromInvoiceDate(), DateConvert.getIsoLocalDateFormatter()));
        RulesChecker.checkRule(new StringToDateFormatRule(query.getToInvoiceDate(), DateConvert.getIsoLocalDateFormatter()));

        LocalDate toInvoiceDate = DateConvert.convertStringToLocalDate(query.getFromInvoiceDate(), DateConvert.getIsoLocalDateFormatter());
        LocalDate fromInvoiceDate = DateConvert.convertStringToLocalDate(query.getToInvoiceDate(), DateConvert.getIsoLocalDateFormatter());

        Map<LocalDate, List<RateDto>> ratesByInvoiceDateMap = this.searchRateBetweenInvoiceDateService.getRoomRates(hotelDto, connectionDto, toInvoiceDate, fromInvoiceDate);
        List<RateByInvoiceDateResponse> rateResponseGrouped = this.convertToResponse(ratesByInvoiceDateMap);

        return new SearchRateBetweenInvoiceDateResponse(query.getHotel(), rateResponseGrouped);
    }

    private ManageHotelDto getManageHotelFromCode(String code){
        return this.searchManageHotelService.getManageHotel(code);
    }

    private ManageTradingCompanyDto getManageTradingCompanyFromHotel(ManageHotelDto hotelDto){
        return this.searchManageTradingCompanyService.searchManageTradingCompany(hotelDto);
    }

    private ManageConnectionDto getConnectionFromTradingCompany(ManageTradingCompanyDto tradingCompanyDto){
        return this.searchConfigurationService.searchConnection(tradingCompanyDto);
    }

    private List<RateByInvoiceDateResponse> convertToResponse(Map<LocalDate, List<RateDto>> ratesByInvoiceDateMap){
        List<RateByInvoiceDateResponse> responseList = new ArrayList<>();

        for(Map.Entry<LocalDate, List<RateDto>> group : ratesByInvoiceDateMap.entrySet()){
            String invoiceDate = DateConvert.convertLocalDateToString(group.getKey(), DateConvert.getIsoLocalDateFormatter());
            RateByInvoiceDateResponse groupedResponse = new RateByInvoiceDateResponse(invoiceDate,
                    group.getValue().stream().map(RateResponse::new).toList());
            responseList.add(groupedResponse);
        }

        return responseList;
    }
}
