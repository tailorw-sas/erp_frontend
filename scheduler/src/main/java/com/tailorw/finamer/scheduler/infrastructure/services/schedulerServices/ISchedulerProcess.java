package com.tailorw.finamer.scheduler.infrastructure.services.schedulerServices;

import com.tailorw.finamer.scheduler.domain.dto.BusinessProcessSchedulerDto;
import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;

import java.time.LocalDate;

public interface ISchedulerProcess {

    void executeProcess(BusinessProcessSchedulerDto scheduler, SchedulerLogDto schedulerLog, LocalDate processingDate);
}
