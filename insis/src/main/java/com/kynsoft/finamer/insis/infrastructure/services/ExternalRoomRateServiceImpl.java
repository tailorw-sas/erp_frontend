package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.utils.DateConvert;
import com.kynsoft.finamer.insis.domain.dto.ExternalRoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IExternalRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.services.http.SyncRoomRateHttpService;
import com.kynsoft.finamer.insis.infrastructure.services.http.entities.response.RateResponse;
import com.kynsoft.finamer.insis.infrastructure.services.http.entities.response.SyncRateByInvoiceDateMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalRoomRateServiceImpl implements IExternalRoomRateService {

    private final SyncRoomRateHttpService rateHttpService;

    public ExternalRoomRateServiceImpl(SyncRoomRateHttpService rateHttpService){
        this.rateHttpService = rateHttpService;
    }

    @Override
    public List<ExternalRoomRateDto> getTcaRoomRatesByInvoiceDateAndHotel(UUID processId,
                                                                          LocalDate invoiceDate,
                                                                          String hotel) {
        SyncRateByInvoiceDateMessage message = this.rateHttpService.syncRoomRatesFromTca(processId, hotel, invoiceDate);
        if(Objects.isNull(message) || Objects.isNull(message.getRateResponses()) || message.getRateResponses().isEmpty()){
            return Collections.emptyList();
        }

        return message.getRateResponses().stream()
                .map(this::convertToExternalRoomRate)
                .collect(Collectors.toList());
    }

    private ExternalRoomRateDto convertToExternalRoomRate(RateResponse rateResponse){
        return new ExternalRoomRateDto(
                rateResponse.getReservationCode(),
                rateResponse.getCouponNumber(),
                DateConvert.convertStringToLocalDate(rateResponse.getCheckInDate(), DateConvert.getIsoLocalDateFormatter()),
                DateConvert.convertStringToLocalDate(rateResponse.getCheckOutDate(), DateConvert.getIsoLocalDateFormatter()),
                rateResponse.getStayDays(),
                rateResponse.getHotelCode(),
                rateResponse.getAgencyCode(),
                rateResponse.getGuestName(),
                rateResponse.getFirstName(),
                rateResponse.getLastName(),
                rateResponse.getTotalNumberOfGuest(),
                rateResponse.getAdults(),
                rateResponse.getChildren(),
                rateResponse.getAmount(),
                rateResponse.getRoomTypeCode(),
                rateResponse.getRatePlanCode(),
                DateConvert.convertStringToLocalDate(rateResponse.getInvoicingDate(), DateConvert.getIsoLocalDateFormatter()),
                DateConvert.convertStringToLocalDate(rateResponse.getHotelCreationDate(), DateConvert.getIsoLocalDateFormatter()),
                rateResponse.getOriginalAmount(),
                rateResponse.getAmountPaymentApplied(),
                rateResponse.getRateByAdult(),
                rateResponse.getRateByChild(),
                rateResponse.getRemarks(),
                rateResponse.getRoomNumber(),
                rateResponse.getHotelInvoiceAmount(),
                rateResponse.getHotelInvoiceNumber(),
                rateResponse.getInvoiceFolioNumber(),
                rateResponse.getQuote(),
                rateResponse.getRenewalNumber(),
                rateResponse.getRoomCategory(),
                rateResponse.getHash()
        );
    }
}
