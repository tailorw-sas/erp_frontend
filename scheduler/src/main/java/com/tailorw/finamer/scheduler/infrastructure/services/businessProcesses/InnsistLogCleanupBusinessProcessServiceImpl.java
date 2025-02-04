package com.tailorw.finamer.scheduler.infrastructure.services.businessProcesses;

import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.domain.services.IFrecuencyService;
import com.tailorw.finamer.scheduler.domain.services.ISchedulerLogService;
import com.tailorw.finamer.scheduler.infrastructure.services.schedulerServices.ISchedulerProcess;
import com.tailorw.finamer.scheduler.infrastructure.services.schedulerServices.SchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InnsistLogCleanupBusinessProcessServiceImpl extends SchedulerService implements ISchedulerProcess {

    private static final String BUSINESS_PROCESS = "INST-LOG-CLEAN";
    //private final ProducerTcaRoomRatesBusinessProcessService producerTcaRoomRatesBusinessProcessService;

    public InnsistLogCleanupBusinessProcessServiceImpl(IBusinessProcessService businessProcessService,
                                                       IBusinessProcessSchedulerService businessProcessSchedulerService,
                                                       ISchedulerLogService logService,
                                                       IFrecuencyService frecuencyService) {
        super(businessProcessService, businessProcessSchedulerService, logService, BUSINESS_PROCESS, frecuencyService);
    }

    @Override
    public void executeProcess(BusinessProcessSchedulerDto scheduler, SchedulerLogDto schedulerLog, LocalDate processingDate) {

    }

    //@Scheduled(fixedRate = 60000, initialDelay = 10000)
    public void processDaily(){
        processDailySchedulers(this::executeProcess);
    }


    protected void processWeekly() {

    }

    @Override
    protected void processOneTime() {

    }
}
