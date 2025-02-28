package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.tailorw.finamer.scheduler.domain.dto.*;
import com.tailorw.finamer.scheduler.domain.rules.SchedulerStatusCodeRule;
import com.tailorw.finamer.scheduler.domain.rules.businessProcess.BusinessProcessInactiveRule;
import com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler.*;
import com.tailorw.finamer.scheduler.domain.services.*;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.EFrequency;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class UpdateBusinessProcessSchedulerCommandHandler implements ICommandHandler<UpdateBusinessProcessSchedulerCommand> {

    private final IFrecuencyService frecuencyService;
    private final IIntervalTypeService intervalTypeService;
    private final IExecutionDateTypeService executionDateTypeService;
    private final IProcessingDateTypeService processingDateTypeService;

    private final IBusinessProcessSchedulerService service;
    private final IBusinessProcessService businessProcessService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public UpdateBusinessProcessSchedulerCommandHandler(IFrecuencyService frecuencyService,
                                                        IIntervalTypeService intervalTypeService,
                                                        IExecutionDateTypeService executionDateTypeService,
                                                        IProcessingDateTypeService processingDateTypeService,
                                                        IBusinessProcessSchedulerService service,
                                                        IBusinessProcessService businessProcessService){
        this.frecuencyService = frecuencyService;
        this.intervalTypeService = intervalTypeService;
        this.executionDateTypeService = executionDateTypeService;
        this.processingDateTypeService = processingDateTypeService;
        this.service = service;
        this.businessProcessService = businessProcessService;
    }

    @Override
    public void handle(UpdateBusinessProcessSchedulerCommand command) {

        BusinessProcessSchedulerDto dto = service.findById(command.getId());

        BusinessProcessDto businessProcessDto = businessProcessService.findById(command.getProcess());

        RulesChecker.checkRule(new BusinessProcessSchedulerFrecuencyIdRule(command.getFrequency(), frecuencyService));
        RulesChecker.checkRule(new BusinessProcessSchedulerIntervalIdRule(command.getIntervalType(), intervalTypeService));

        FrequencyDto frequencyDto = frecuencyService.getById(command.getFrequency());
        RulesChecker.checkRule(new BusinessProcessSchedulerExecutionDateCodeRule(frequencyDto.getCode(), command.getExecutionDateType(), executionDateTypeService));
        RulesChecker.checkRule(new BusinessProcessSchedulerExecutionDateFormatRule(command.getExecutionDate()));
        RulesChecker.checkRule(new BusinessProcessSchedulerExecutionTimeFormatRule(command.getExecutionTime()));
        RulesChecker.checkRule(new BusinessProcessSchedulerProcessingDateCodeRule(command.getProcessingDateType(), processingDateTypeService));
        RulesChecker.checkRule(new SchedulerStatusCodeRule(command.getStatus()));
        RulesChecker.checkRule(new BusinessProcessInactiveRule(businessProcessDto));


        IntervalTypeDto intervalTypeDto = intervalTypeService.getById(command.getIntervalType());

        ExecutionDateTypeDto executionDateTypeDto = getExecutionDateType(command.getExecutionDateType(), frequencyDto.getCode());
        ProcessingDateTypeDto processingDateTypeDto = processingDateTypeService.getById(command.getProcessingDateType());

        dto.setFrequency(frequencyDto);
        dto.setIntervalType(intervalTypeDto);
        dto.setInterval(command.getInterval());
        dto.setExecutionDateType(executionDateTypeDto);
        dto.setExecutionDateValue(command.getExecutionDateValue());
        dto.setExecutionDate(convertToDate(command.getExecutionDate()));
        dto.setExecutionTime(convertToTime(command.getExecutionTime()));
        dto.setProcessingDateType(processingDateTypeDto);
        dto.setProcessingDateValue(command.getProcessingDateValue());
        dto.setProcessingDate(convertToDate(command.getProcessingDate()));
        dto.setProcess(businessProcessDto);
        dto.setAllowsQueueing(command.isAllowsQueueing());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setParams, command.getParams(), dto.getParams(), update::setUpdate);

        service.update(dto);
    }

    private ExecutionDateTypeDto getExecutionDateType(UUID id, String frequency){
        if(frequency.equals(EFrequency.DAILY.name())){
            return null;
        }
        return executionDateTypeService.getById(id);
    }

    private LocalDate convertToDate(String date){
        if(date == null || date.isEmpty()){
            return null;
        }
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    private LocalTime convertToTime(String time){
        if(time == null || time.isEmpty()){
            return null;
        }
        return LocalTime.parse(time, TIME_FORMATTER);
    }
}
