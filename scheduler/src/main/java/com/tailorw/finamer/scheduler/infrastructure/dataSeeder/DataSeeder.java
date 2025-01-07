package com.tailorw.finamer.scheduler.infrastructure.dataSeeder;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.tailorw.finamer.scheduler.domain.dto.*;
import com.tailorw.finamer.scheduler.domain.services.*;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements ApplicationRunner {

    private final IFrecuencyService frecuencyService;
    private final IIntervalTypeService intervalTypeService;
    private final IExecutionDateTypeService executionDateTypeService;
    private final IProcessingDateTypeService processingDateTypeService;
    private final IBusinessProcessSchedulerRuleService businessProcessSchedulerRuleService;

    private static final Map<String, Class<? extends Enum<?>>> ENUM_MAP = Map.of(
            "Frequency", EFrequency.class,
            "IntervalType", EIntervalType.class,
            "ExecutionDateType", EExecutionDateType.class,
            "ProcessingDateType", EProcessingDateType.class
    );

    public DataSeeder(IFrecuencyService frecuencyService,
                      IIntervalTypeService intervalTypeService,
                      IExecutionDateTypeService executionDateTypeService,
                      IProcessingDateTypeService processingDateTypeService,
                      IBusinessProcessSchedulerRuleService businessProcessSchedulerRuleService){
        this.frecuencyService = frecuencyService;
        this.intervalTypeService = intervalTypeService;
        this.executionDateTypeService = executionDateTypeService;
        this.processingDateTypeService = processingDateTypeService;
        this.businessProcessSchedulerRuleService = businessProcessSchedulerRuleService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createFrequencies();
        createIntervalTypes();
        createExecutionDateTypes();
        createProcessingDateTypes();

        createOnetimeFrequencyRules();
        createDailyFrequencyRules();
    }

    private void createFrequencies(){
        List<String> frequencyList = convertEnumToStringListByName("Frequency");
        frequencyList.forEach(frequency -> {
            FrequencyDto frequencyDto = frecuencyService.getByCode(frequency);
            if(Objects.isNull(frequencyDto)){
                FrequencyDto newFrequency = new FrequencyDto(UUID.randomUUID(), frequency, Status.ACTIVE);
                frecuencyService.create(newFrequency);
            }
        });
    }

    private void createIntervalTypes(){
        List<String> intervalTypeList = convertEnumToStringListByName("IntervalType");
        intervalTypeList.forEach(intervalType -> {
            IntervalTypeDto intervalTypeDto = intervalTypeService.getByCode(intervalType);
            if(Objects.isNull(intervalTypeDto)){
                IntervalTypeDto newIntervalType = new IntervalTypeDto(UUID.randomUUID(), intervalType, Status.ACTIVE);
                intervalTypeService.create(newIntervalType);
            }
        });
    }

    private void createExecutionDateTypes(){
        List<String> executionDateTypeList = convertEnumToStringListByName("ExecutionDateType");
        executionDateTypeList.forEach(executionDateType -> {
            ExecutionDateTypeDto executionDateTypeDto = executionDateTypeService.getByCode(executionDateType);
            if(Objects.isNull(executionDateTypeDto)){
                ExecutionDateTypeDto newExecutionDateType = new ExecutionDateTypeDto(UUID.randomUUID(), executionDateType, Status.ACTIVE);
                executionDateTypeService.create(newExecutionDateType);
            }
        });
    }

    private void createProcessingDateTypes(){
        List<String> processingDateTypeList = convertEnumToStringListByName("ProcessingDateType");
        processingDateTypeList.forEach(processingDateType -> {
            ProcessingDateTypeDto processingDateTypeDto = processingDateTypeService.getByCode(processingDateType);
            if(Objects.isNull(processingDateTypeDto)){
                ProcessingDateTypeDto newProcessingDateType = new ProcessingDateTypeDto(UUID.randomUUID(), processingDateType, Status.ACTIVE);
                processingDateTypeService.create(newProcessingDateType);
            }
        });
    }

    public static List<String> convertEnumToStringListByName(String enumName) {
        Class<? extends Enum<?>> enumClass = ENUM_MAP.get(enumName);
        if (enumClass == null) {
            throw new IllegalArgumentException("Enum no registrado: " + enumName);
        }
        return convertEnumToStringList(enumClass);
    }

    public static <T extends Enum<T>> List<String> convertEnumToStringList(Class<? extends Enum<?>> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public void createOnetimeFrequencyRules(){
         createSchedulerRuleIfNotExists(new BusinessProcessSchedulerRuleDto(UUID.fromString("4164e438-994e-4990-b3ef-43552bee16aa"), getFrequency(EFrequency.ONE_TIME.name()), getIntervalType(EIntervalType.ONE_TIME.name()), false, getExecutionDateType(EExecutionDateType.CUSTOM.name()), false, true, true, Status.ACTIVE));
         createSchedulerRuleIfNotExists(new BusinessProcessSchedulerRuleDto(UUID.fromString("bf337656-dab3-4c94-96f5-2ba08ce33264"), getFrequency(EFrequency.ONE_TIME.name()), getIntervalType(EIntervalType.MINUTE.name()), true, getExecutionDateType(EExecutionDateType.CUSTOM.name()), false, true, true, Status.ACTIVE));
         createSchedulerRuleIfNotExists(new BusinessProcessSchedulerRuleDto(UUID.fromString("31692172-603b-48b4-affd-e3429c9fc74b"), getFrequency(EFrequency.ONE_TIME.name()), getIntervalType(EIntervalType.HOUR.name()), true, getExecutionDateType(EExecutionDateType.CUSTOM.name()), false, true, true, Status.ACTIVE));
    }

    private void createDailyFrequencyRules(){
        createSchedulerRuleIfNotExists(new BusinessProcessSchedulerRuleDto(UUID.fromString("3804c5bd-7651-4323-8204-36e5f4063e4d"), getFrequency(EFrequency.DAILY.name()), getIntervalType(EIntervalType.ONE_TIME.name()), false, null, false, false, false, Status.ACTIVE));
        createSchedulerRuleIfNotExists(new BusinessProcessSchedulerRuleDto(UUID.fromString("3bdda4cc-e30d-44ad-bbab-db30591182f7"), getFrequency(EFrequency.DAILY.name()), getIntervalType(EIntervalType.MINUTE.name()), true, null, false, false, false, Status.ACTIVE));
        createSchedulerRuleIfNotExists(new BusinessProcessSchedulerRuleDto(UUID.fromString("8bc0fa8f-c6e6-48f5-b471-3a1a19df3086"), getFrequency(EFrequency.DAILY.name()), getIntervalType(EIntervalType.HOUR.name()), true, null, false, false, false, Status.ACTIVE));
    }

    private void createSchedulerRuleIfNotExists(BusinessProcessSchedulerRuleDto schedulerRule){
        try{
            businessProcessSchedulerRuleService.getById(schedulerRule.getId());
        } catch (BusinessNotFoundException e) {
            businessProcessSchedulerRuleService.create(schedulerRule);
        }
    }

    private FrequencyDto getFrequency(String code){
        return frecuencyService.getByCode(code);
    }
    private IntervalTypeDto getIntervalType(String code){
        return intervalTypeService.getByCode(code);
    }
    private ExecutionDateTypeDto getExecutionDateType(String code){
        return executionDateTypeService.getByCode(code);
    }
}
