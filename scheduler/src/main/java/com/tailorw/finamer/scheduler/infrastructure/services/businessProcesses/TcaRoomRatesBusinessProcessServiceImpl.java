package com.tailorw.finamer.scheduler.infrastructure.services.businessProcesses;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.domain.services.IFrecuencyService;
import com.tailorw.finamer.scheduler.domain.services.ISchedulerLogService;
import com.tailorw.finamer.scheduler.infrastructure.model.kafka.SyncRatesByInvoiceDateKafka;
import com.tailorw.finamer.scheduler.infrastructure.model.businessProcess.parameters.HotelParameter;
import com.tailorw.finamer.scheduler.infrastructure.services.kafka.producer.ProducerTcaRoomRatesBusinessProcessService;
import com.tailorw.finamer.scheduler.infrastructure.services.schedulerServices.ISchedulerProcess;
import com.tailorw.finamer.scheduler.infrastructure.services.schedulerServices.SchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TcaRoomRatesBusinessProcessServiceImpl extends SchedulerService implements ISchedulerProcess {

    private static final String BUSINESS_PROCESS = "TCA-RR";
    private final ProducerTcaRoomRatesBusinessProcessService producerTcaRoomRatesBusinessProcessService;

    public TcaRoomRatesBusinessProcessServiceImpl(IBusinessProcessService businessProcessService,
                                                  IBusinessProcessSchedulerService businessProcessSchedulerService,
                                                  ISchedulerLogService schedulerLogService,
                                                  ProducerTcaRoomRatesBusinessProcessService producerTcaRoomRatesBusinessProcessService,
                                                  IFrecuencyService frecuencyService) {
        super(businessProcessService, businessProcessSchedulerService, schedulerLogService, BUSINESS_PROCESS, frecuencyService);
        this.producerTcaRoomRatesBusinessProcessService = producerTcaRoomRatesBusinessProcessService;
    }

    @Override
    public void executeProcess(BusinessProcessSchedulerDto scheduler, SchedulerLogDto schedulerLog, LocalDate processingDate) {
        //Logica para mi implementacion:

        //processingDate = LocalDate.of(2024, 5, 2);
        //Leer los hoteles a sincronizar
        List<String> hotels = getHotelsFromParams(scheduler.getParams()).stream()
                .map(hotelParameter -> {
                    return hotelParameter.getHotel();
                }).toList();

        //Generar la entidad con los datos a procesar
        SyncRatesByInvoiceDateKafka syncProcess = new SyncRatesByInvoiceDateKafka(
                schedulerLog.getId(),
                hotels,
                processingDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        //Enviar al kafka la petición de sincronización
        producerTcaRoomRatesBusinessProcessService.create(syncProcess);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    public void processDaily(){
        processDailySchedulers(this::executeProcess);
    }

    //@Scheduled(fixedRate = 60000, initialDelay = 10000)
    protected void processWeekly() {
        processWeeklySchedulers(this::executeProcess);
    }

    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    protected void processOneTime() {
        processOneTimeSchedulers(this::executeProcess);
    }

    private List<HotelParameter> getHotelsFromParams(String params){
        try{ObjectMapper mapper = new ObjectMapper();
            List<HotelParameter> hotels = mapper.readValue(params, new TypeReference<List<HotelParameter>>() {});
            return hotels;
        }catch (Exception ex){
            return null;
        }
    }
}
