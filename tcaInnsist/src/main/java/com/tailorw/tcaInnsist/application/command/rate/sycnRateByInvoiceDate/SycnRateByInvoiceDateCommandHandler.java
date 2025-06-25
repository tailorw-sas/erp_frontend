package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.scheduler.UpdateSchedulerProcessKafka;
import com.tailorw.tcaInnsist.application.query.objectResponse.RateResponse;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import com.tailorw.tcaInnsist.domain.services.IRateService;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.GroupedRatesKafka;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.ManageRateKafka;
import com.tailorw.tcaInnsist.infrastructure.service.kafka.producer.ProducerReplicateGroupedRatesService;
import com.tailorw.tcaInnsist.infrastructure.service.kafka.producer.ProducerUpdateSchedulerLogService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class SycnRateByInvoiceDateCommandHandler implements ICommandHandler<SycnRateByInvoiceDateCommand> {

    private final IRateService service;
    private final IManageHotelService hotelService;
    private final IManageTradingCompanyService tradingCompanyService;
    private final IManageConnectionService configurationService;
    private final ProducerReplicateGroupedRatesService producerReplicateGroupedRatesService;
    private final ProducerUpdateSchedulerLogService producerUpdateSchedulerLogService;


    @Override
    public void handle(SycnRateByInvoiceDateCommand command) {
        StringBuilder additionDetails = new StringBuilder();

        for(String hotelCode : command.getHotelList()){
            try{
                ManageHotelDto hotelDto = this.validateHotel(hotelCode);
                ManageTradingCompanyDto tradingCompanyDto = this.validateTradingCompany(hotelDto);
                ManageConnectionDto connection = this.validateConnection(tradingCompanyDto);

                List<RateDto> rateDtos = this.getRoomRates(hotelDto, connection, command.getInvoiceDate());
                if(command.getManual()){
                    List<RateResponse> rateResponses = this.prepareResponse(rateDtos);
                    command.setRateResponses(rateResponses);
                }else{
                    this.syncRoomRates(command.getProcessId(), command.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")), hotelCode, rateDtos);
                }
            }catch (IllegalArgumentException ex) {
                logWarningAndAppend(additionDetails, ex.getMessage());
            }
        }

        if(!command.getManual()){
            setLogProcessAsCompleted(command.getProcessId(), additionDetails.toString());
        }

        logError("Sync Rate Process End.", Level.INFO, null);
        logError("**************************************************************", Level.INFO, null);
    }

    private ManageHotelDto validateHotel(String hotelCode){
        ManageHotelDto hotel = hotelService.getByCode(hotelCode);

        if(Objects.isNull(hotel)){
            throw new IllegalArgumentException(String.format("The hotel %s does not exist in the TcaInnsist database.", hotelCode));
        }
        if(Objects.isNull(hotel.getTradingCompanyId())){
            throw new IllegalArgumentException(String.format("The hotel %s does not have a trading company associated.", hotel.getCode()));
        }

        return  hotel;
    }

    private ManageTradingCompanyDto validateTradingCompany(ManageHotelDto hotelDto){
        ManageTradingCompanyDto tradingCompany = tradingCompanyService.getById(hotelDto.getTradingCompanyId());

        if(Objects.isNull(tradingCompany)){
            throw new IllegalArgumentException(String.format("The trading company %s does not exist in the TcaInnsist database.", hotelDto.getTradingCompanyId()));
        }

        if(Objects.isNull(tradingCompany.getConnectionId()) ){
            throw new IllegalArgumentException(String.format("The trading company %s does not have a valid connection.", tradingCompany.getCode()));
        }

        return tradingCompany;
    }

    private ManageConnectionDto validateConnection(ManageTradingCompanyDto tradingCompanyDto){
        ManageConnectionDto connection = configurationService.getById(tradingCompanyDto.getConnectionId());
        if(Objects.isNull(connection)){
            throw new IllegalArgumentException(String.format("The Connection ID %s does not exist in the TcaInnsist database.", tradingCompanyDto.getConnectionId()));
        }
        return connection;
    }

    private List<RateDto> getRoomRates(ManageHotelDto hotel, ManageConnectionDto configuration, LocalDate invoiceDate){
        List<RateDto> rateDtos = new ArrayList<>();
        try{
            rateDtos = service.findByInvoiceDate(hotel, configuration, invoiceDate);

        } catch (RuntimeException e) {
            String message = String.format("Error while trying to get rates from %s hotel with invoiceDate: %s", hotel.getCode(), invoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            logError(message, Level.WARNING, e);
            throw new IllegalArgumentException(message);
        }

        if(rateDtos.isEmpty()){
            throw new IllegalArgumentException(String.format("The hotel %s does not contain room rates for %s invoice date", hotel.getCode(), invoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        }

        return rateDtos;
    }

    private void syncRoomRates(UUID processId, String invoiceDate, String hotelCode, List<RateDto> rates){
        UUID idLog = UUID.randomUUID();

        GroupedRatesKafka groupedRatesKafka = new GroupedRatesKafka(idLog,
                processId,
                invoiceDate,
                hotelCode,
                rates.stream().map(ManageRateKafka::new).toList()
        );
        producerReplicateGroupedRatesService.create(groupedRatesKafka);
    }

    private List<RateResponse> prepareResponse(List<RateDto> rates){
        return rates.stream()
                .map(RateResponse::new)
                .toList();
    }

    private void setLogProcessAsCompleted(UUID processId, String message){
        producerUpdateSchedulerLogService.update(new UpdateSchedulerProcessKafka(
                processId,
                LocalDateTime.now(),
                message
        ));
    }

    private void logWarningAndAppend(StringBuilder details, String message) {
        logError(message, Level.INFO, null);
        details.append(message).append("\n");
    }

    private void logError(String message, Level level, Throwable thrown){
        Logger.getLogger(SycnRateByInvoiceDateCommandHandler.class.getName()).log(level, message, thrown);
    }
}
