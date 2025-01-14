package com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.scheduler.UpdateSchedulerProcessKafka;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
                ManageHotelDto hotelDto = validateHotel(hotelCode);
                ManageTradingCompanyDto tradingCompanyDto = validateTradingCompany(hotelDto);
                ManageConnectionDto connection = validateConnection(tradingCompanyDto);

                Map<String, List<RateDto>> groupedRates = getGroupedRoomRates(hotelDto, connection, command.getInvoiceDate());
                syncRoomRates(command.getProcessId(), command.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")), hotelCode, groupedRates);
            }catch (IllegalArgumentException ex) {
                logWarningAndAppend(additionDetails, ex.getMessage());
            }
        }

        setLogProcessAsCompleted(command.getProcessId(), additionDetails.toString());
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
            throw new IllegalArgumentException(String.format("The trading company %s does not have a valid connection.", hotelDto.getTradingCompanyId()));
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

    private Map<String, List<RateDto>> getGroupedRoomRates(ManageHotelDto hotel, ManageConnectionDto configuration, LocalDate invoiceDate){
        List<RateDto> rateDtos = service.findByInvoiceDate(hotel, configuration, invoiceDate);

        if(rateDtos.isEmpty()){
            throw new IllegalArgumentException(String.format("The hotel %s does not containts room rates for %s invoice date", hotel.getCode(), invoiceDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        }

        return rateDtos.stream()
                .collect(Collectors.groupingBy(r -> r.getReservationCode() + "|" + r.getCouponNumber()));
    }

    private void syncRoomRates(UUID processId, String invoiceDate, String hotelCode, Map<String, List<RateDto>> groupedRates){
        UUID idLog = UUID.randomUUID();
        int count = 1;
        for(Map.Entry<String, List<RateDto>> entry : groupedRates.entrySet()){
            GroupedRatesKafka groupedRatesKafka = new GroupedRatesKafka(
                    idLog,
                    processId,
                    invoiceDate,
                    hotelCode,
                    entry.getKey().split("\\|")[0],
                    entry.getKey().split("\\|")[1],
                    entry.getValue().stream()
                            .map(ManageRateKafka::new)
                            .collect(Collectors.toList()),
                    count == 1,
                    groupedRates.size() == count
            );
            producerReplicateGroupedRatesService.create(groupedRatesKafka);
            count++;
        }
    }

    private void setLogProcessAsCompleted(UUID processId, String message){
        producerUpdateSchedulerLogService.update(new UpdateSchedulerProcessKafka(
                processId,
                LocalDateTime.now(),
                message
        ));
    }

    private void logWarningAndAppend(StringBuilder details, String message) {
        Logger.getLogger(SycnRateByInvoiceDateCommandHandler.class.getName()).log(Level.WARNING, message);
        details.append(message).append("\n");
    }
}
