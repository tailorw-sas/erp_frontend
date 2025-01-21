package com.tailorw.tcaInnsist.application.command.rate.syncRateBetweenInvoiceDate;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.scheduler.UpdateSchedulerProcessKafka;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.dto.RateDto;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.*;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.GroupedRatesKafka;
import com.tailorw.tcaInnsist.infrastructure.model.kafka.ManageRateKafka;
import com.tailorw.tcaInnsist.infrastructure.service.kafka.producer.ProducerReplicateGroupedRatesService;
import com.tailorw.tcaInnsist.infrastructure.service.kafka.producer.ProducerReplicateRateService;
import com.tailorw.tcaInnsist.infrastructure.service.kafka.producer.ProducerUpdateSchedulerLogService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SyncRateBetweenInvoiceDateCommandHandler implements ICommandHandler<SyncRateBetweenInvoiceDateCommand> {

    private final IRateService service;
    private final IManageHotelService hotelService;
    private final IManageTradingCompanyService tradingCompanyService;
    private final IManageConnectionService configurationService;
    private final IManageRateService manageRateService;
    private final ProducerReplicateRateService producerReplicateRateService;
    private final ProducerReplicateGroupedRatesService producerReplicateGroupedRatesService;
    private final ProducerUpdateSchedulerLogService producerUpdateSchedulerLogService;

    @Override
    public void handle(SyncRateBetweenInvoiceDateCommand command) {
        ManageHotelDto hotel = hotelService.getByCode(command.getHotel());
        if(Objects.isNull(hotel)){
            return;
        }
        ManageTradingCompanyDto tradingCompany = tradingCompanyService.getById(hotel.getTradingCompanyId());
        if(Objects.isNull(tradingCompany)){
            return;
        }

        ManageConnectionDto configuration = configurationService.getById(tradingCompany.getConnectionId());
        if(Objects.isNull(configuration)){
            return;
        }

        UUID idLog = UUID.randomUUID();
        List<RateDto> rateDtos = service.findBetweenInvoiceDates(hotel, configuration, command.getStartDate(), command.getEndDate());

        Map<LocalDate, List<RateDto>> groupedRatesByInvoiceDate = rateDtos
                .stream().collect(Collectors.groupingBy(r -> r.getInvoicingDate()));
        int index = 0;

        for(Map.Entry<LocalDate, List<RateDto>> entryByDate : groupedRatesByInvoiceDate.entrySet()){
            index++;

            Map<String, List<RateDto>> groupedRates = entryByDate.getValue()
                    .stream()
                    .collect(Collectors.groupingBy(r -> r.getReservationCode() + "|" + r.getCouponNumber() + "|" + r.getInvoicingDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))));

            for(Map.Entry<String, List<RateDto>> entry : groupedRates.entrySet()){
                GroupedRatesKafka groupedRatesKafka = new GroupedRatesKafka(
                        idLog,
                        command.getProcessId(),
                        entryByDate.getKey().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                        command.getHotel(),
                        entry.getKey().split("\\|")[0],
                        entry.getKey().split("\\|")[1],
                        entry.getValue().stream()
                                .map(ManageRateKafka::new)
                                .collect(Collectors.toList()),
                        index == 1,
                        groupedRatesByInvoiceDate.size() == index
                );
                producerReplicateGroupedRatesService.create(groupedRatesKafka);
            }

        }

        //Enviar mensaje a Scheduler de que se acabó el proceso
        if(command.isLastGroup()){
            producerUpdateSchedulerLogService.update(new UpdateSchedulerProcessKafka(
                    command.getProcessId(),
                    LocalDateTime.now(),
                    ""
            ));
        }
    }
}
