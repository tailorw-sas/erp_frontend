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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
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
        ManageHotelDto hotel = hotelService.getByCode(command.getHotel());
        if(Objects.isNull(hotel)){
            return;
        }
        ManageTradingCompanyDto tradingCompany = tradingCompanyService.getById(hotel.getTradingCompanyId());
        if(Objects.isNull(tradingCompany) || Objects.isNull(tradingCompany.getConnectionId()) ){
            return;
        }

        ManageConnectionDto configuration = configurationService.getById(tradingCompany.getConnectionId());
        if(Objects.isNull(configuration)){
            return;
        }

        UUID idLog = UUID.randomUUID();

        List<RateDto> rateDtos = service.findByInvoiceDate(hotel, configuration, command.getInvoiceDate());

        Map<String, List<RateDto>> groupedRates = rateDtos
                .stream()
                .collect(Collectors.groupingBy(r -> r.getReservationCode() + "|" + r.getCouponNumber()));

        int count = 1;
        for(Map.Entry<String, List<RateDto>> entry : groupedRates.entrySet()){
            GroupedRatesKafka groupedRatesKafka = new GroupedRatesKafka(
                    idLog,
                    command.getProcessId(),
                    command.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                    command.getHotel(),
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
