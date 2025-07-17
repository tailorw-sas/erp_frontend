package com.tailorw.tcaInnsist.application.service.rate.search;

import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateCommandHandler;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import com.tailorw.tcaInnsist.domain.services.IRateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SearchRateBetweenInvoiceDateService {

    private final IRateService rateService;

    public SearchRateBetweenInvoiceDateService(IRateService rateService){
        this.rateService = rateService;
    }

    public Map<LocalDate, List<RateDto>> getRoomRates(ManageHotelDto hotel, ManageConnectionDto configuration, LocalDate fromInvoiceDate, LocalDate toInvoiceDate){
        List<RateDto> rateDtos = new ArrayList<>();
        try{
            rateDtos = rateService.findBetweenInvoiceDates(hotel, configuration, fromInvoiceDate, toInvoiceDate);

        } catch (RuntimeException e) {
            String message = String.format("Error while trying to get rates of %s hotel from invoiceDate: %s to: %s", hotel.getCode(),
                    fromInvoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    toInvoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            logError(message, Level.WARNING, e);
            throw new IllegalArgumentException(message);
        }

        if(rateDtos.isEmpty()){
            throw new IllegalArgumentException(String.format("The hotel %s does not contain room rates from %s invoice date to %s", hotel.getCode(),
                    fromInvoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    toInvoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        }

        return this.getResultMap(rateDtos);
    }

    private void logError(String message, Level level, Throwable thrown){
        Logger.getLogger(SycnRateByInvoiceDateCommandHandler.class.getName()).log(level, message, thrown);
    }

    private Map<LocalDate, List<RateDto>> getResultMap(List<RateDto> rates){
        return rates.stream()
                .collect(Collectors.groupingBy(RateDto::getInvoicingDate));
    }
}
