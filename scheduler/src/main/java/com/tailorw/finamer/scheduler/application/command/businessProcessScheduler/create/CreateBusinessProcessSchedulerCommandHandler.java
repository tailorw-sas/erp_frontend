package com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.tailorw.finamer.scheduler.domain.dto.*;
import com.tailorw.finamer.scheduler.domain.rules.businessProcess.BusinessProcessInactiveRule;
import com.tailorw.finamer.scheduler.domain.rules.businessProcessScheduler.*;
import com.tailorw.finamer.scheduler.domain.services.*;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class CreateBusinessProcessSchedulerCommandHandler implements ICommandHandler<CreateBusinessProcessSchedulerCommand> {

    private final IFrecuencyService frecuencyService;
    private final IIntervalTypeService intervalTypeService;
    private final IExecutionDateTypeService executionDateTypeService;
    private final IProcessingDateTypeService processingDateTypeService;
    private final IBusinessProcessSchedulerService service;
    private final IBusinessProcessService businessProcessService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FULL_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public CreateBusinessProcessSchedulerCommandHandler(IFrecuencyService frecuencyService,
                                                        IIntervalTypeService intervalTypeService,
                                                        IExecutionDateTypeService executionDateTypeService,
                                                        IProcessingDateTypeService processingDateTypeService,
                                                        IBusinessProcessSchedulerService service,
                                                        IBusinessProcessService businessProcessService,
                                                        ObjectMapper mapper){
        this.frecuencyService = frecuencyService;
        this.intervalTypeService = intervalTypeService;
        this.executionDateTypeService = executionDateTypeService;
        this.processingDateTypeService = processingDateTypeService;
        this.service = service;
        this.businessProcessService = businessProcessService;
    }

    @Override
    public void handle(CreateBusinessProcessSchedulerCommand command) {

        BusinessProcessDto businessProcessDto = businessProcessService.findById(command.getProcess());

        FrequencyDto frequencyDto = frecuencyService.getById(command.getFrequency());
        IntervalTypeDto intervalTypeDto = intervalTypeService.getById(command.getIntervalType());
        RulesChecker.checkRule(new BusinessProcessSchedulerExecutionDateCodeRule(frequencyDto.getCode(), command.getExecutionDateType(), executionDateTypeService));

        RulesChecker.checkRule(new BusinessProcessSchedulerExecutionDateFormatRule(command.getExecutionDate()));
        RulesChecker.checkRule(new BusinessProcessSchedulerExecutionTimeFormatRule(command.getExecutionTime()));

        //Implementar rule para executionDateValue que sea 01 .. 28 o 01/01 a 12/31
        RulesChecker.checkRule(new BusinessProcessSchedulerProcessingDateCodeRule(command.getProcessingDateType(), processingDateTypeService));
        RulesChecker.checkRule(new BusinessProcessInactiveRule(businessProcessDto));

        RulesChecker.checkRule(new BusinessProcessSchedulerTimeFormatRule(command.getStartTime()));
        RulesChecker.checkRule(new BusinessProcessSchedulerTimeFormatRule(command.getEndTime()));

        ExecutionDateTypeDto executionDateTypeDto = getExecutionDateType(command.getExecutionDateType(), frequencyDto.getCode());
        ProcessingDateTypeDto processingDateTypeDto = processingDateTypeService.getById(command.getProcessingDateType());

        BusinessProcessSchedulerDto dto = new BusinessProcessSchedulerDto(
                command.getId(),
                frequencyDto,
                intervalTypeDto,
                command.getInterval(),
                executionDateTypeDto,
                command.getExecutionDateValue(),
                convertToDate(command.getExecutionDate()),
                convertToTime(command.getExecutionTime()),
                processingDateTypeDto,
                command.getProcessingDateValue(),
                convertToDate(command.getProcessingDate()),
                Status.ACTIVE,
                command.getParams(),
                null,
                false,
                businessProcessDto,
                null,
                null,
                command.isAllowsQueueing(),
                convertToTime(command.getStartTime(), LocalTime.MIDNIGHT),
                convertToTime(command.getEndTime(), LocalTime.of(23, 59, 59))
        );

        service.create(dto);
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

    private LocalTime convertToTime(String time, LocalTime defaultTime){
        if(time == null || time.isEmpty()){
            return defaultTime;
        }

        return LocalTime.parse(time, FULL_TIME_FORMATTER);
    }
}
