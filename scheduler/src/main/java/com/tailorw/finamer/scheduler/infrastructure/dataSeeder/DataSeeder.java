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

    private final IBusinessProcessService businessProcessService;
    private final IBusinessProcessSchedulerService businessProcessSchedulerService;

    private final IBusinessProcessSchedulerExecutionRuleService businessProcessSchedulerRuleService;
    private final IBusinessProcessSchedulerProcessingRuleService businessProcessSchedulerProcessingRuleService;

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
                      IBusinessProcessService businessProcessService,
                      IBusinessProcessSchedulerService businessProcessSchedulerService,
                      IBusinessProcessSchedulerExecutionRuleService businessProcessSchedulerRuleService,
                      IBusinessProcessSchedulerProcessingRuleService businessProcessSchedulerProcessingRuleService){
        this.frecuencyService = frecuencyService;
        this.intervalTypeService = intervalTypeService;
        this.executionDateTypeService = executionDateTypeService;
        this.processingDateTypeService = processingDateTypeService;
        this.businessProcessService = businessProcessService;
        this.businessProcessSchedulerService = businessProcessSchedulerService;
        this.businessProcessSchedulerRuleService = businessProcessSchedulerRuleService;
        this.businessProcessSchedulerProcessingRuleService = businessProcessSchedulerProcessingRuleService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createFrequencies();
        createIntervalTypes();
        createExecutionDateTypes();
        createProcessingDateTypes();



        createOnetimeFrequencyRules();
        createDailyFrequencyRules();

        createBusinessProcessSchedulerProcessingRules();
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

    public void createBusinessProcesses(){
        createBusinesProcessIfNotExists(new BusinessProcessDto(UUID.fromString("8cf67fd5-dc3f-412f-8960-653ca955e2a0"), "TCA-RR", "TCA-ROOM-RATE-REPLICATE", "Proceso de replicacion de room rates de TCA a Innsist", Status.ACTIVE, null, null));
    }

    public void createOnetimeFrequencyRules(){
         createSchedulerRuleIfNotExists(new BusinessProcessSchedulerExecutionRuleDto(UUID.fromString("4164e438-994e-4990-b3ef-43552bee16aa"), getFrequency(EFrequency.ONE_TIME.name()), getIntervalType(EIntervalType.ONE_TIME.name()), false, true, getExecutionDateType(EExecutionDateType.CUSTOM.name()), false, true, true, Status.ACTIVE));
         createSchedulerRuleIfNotExists(new BusinessProcessSchedulerExecutionRuleDto(UUID.fromString("bf337656-dab3-4c94-96f5-2ba08ce33264"), getFrequency(EFrequency.ONE_TIME.name()), getIntervalType(EIntervalType.MINUTE.name()), true, true, getExecutionDateType(EExecutionDateType.CUSTOM.name()), false, true, true, Status.ACTIVE));
         createSchedulerRuleIfNotExists(new BusinessProcessSchedulerExecutionRuleDto(UUID.fromString("31692172-603b-48b4-affd-e3429c9fc74b"), getFrequency(EFrequency.ONE_TIME.name()), getIntervalType(EIntervalType.HOUR.name()), true, true, getExecutionDateType(EExecutionDateType.CUSTOM.name()), false, true, true, Status.ACTIVE));
    }

    private void createDailyFrequencyRules(){
        createSchedulerRuleIfNotExists(new BusinessProcessSchedulerExecutionRuleDto(UUID.fromString("3804c5bd-7651-4323-8204-36e5f4063e4d"), getFrequency(EFrequency.DAILY.name()), getIntervalType(EIntervalType.ONE_TIME.name()), false, false, null, false, false, false, Status.ACTIVE));
        createSchedulerRuleIfNotExists(new BusinessProcessSchedulerExecutionRuleDto(UUID.fromString("3bdda4cc-e30d-44ad-bbab-db30591182f7"), getFrequency(EFrequency.DAILY.name()), getIntervalType(EIntervalType.MINUTE.name()), true, false, null, false, false, false, Status.ACTIVE));
        createSchedulerRuleIfNotExists(new BusinessProcessSchedulerExecutionRuleDto(UUID.fromString("8bc0fa8f-c6e6-48f5-b471-3a1a19df3086"), getFrequency(EFrequency.DAILY.name()), getIntervalType(EIntervalType.HOUR.name()), true, false, null, false, false, false, Status.ACTIVE));
    }

    private void createBusinessProcessSchedulerProcessingRules(){
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("0ae92638-69a1-4d17-9486-3f868e923769"), getFrequency(EFrequency.DAILY.name()), getProcessingDateType(EProcessingDateType.TODAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("c442ed82-b7c6-4768-91f8-65584ec1e88a"), getFrequency(EFrequency.DAILY.name()), getProcessingDateType(EProcessingDateType.YESTERDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("47b5d45a-99e1-4e55-a210-b8cf6504b239"), getFrequency(EFrequency.DAILY.name()), getProcessingDateType(EProcessingDateType.BEFORE_YESTERDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("1c82b5e6-8f8b-47c3-8dfa-57c5f1d34ba9"), getFrequency(EFrequency.DAILY.name()), getProcessingDateType(EProcessingDateType.DAY.name()), true, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("dd3755b2-315f-4060-835a-014a45c40301"), getFrequency(EFrequency.DAILY.name()), getProcessingDateType(EProcessingDateType.CUSTOM.name()), false, true, Status.ACTIVE));

        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("ada46457-7b41-475e-a0ed-bf0dca3ffd17"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_MONDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("e2ebfbbe-f718-4cf5-a922-d4c665cb8403"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_TUESDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("cd418799-87d7-4d86-a733-b24de99f986b"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_WEDNESDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("bbc8ae03-8e50-4e5a-8848-66d6d811c5be"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_THURSDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("003e1573-d6b3-4655-a773-e8855a24dbc4"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_FRIDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("89fcd232-5e15-4764-b088-760429080640"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_SATURDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("da4d4673-e8dd-4bde-a69b-31602d835999"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_SUNDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("9e083c3c-b26d-437a-8d35-20e6bf503a06"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.FIRST_DAY_OF_WEEK.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("0abdb08a-7749-4a08-9912-505e0cc2b265"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.LAST_DAY_OF_THE_PREVIOUS_WEEK.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("8328a305-4c4b-4ba6-b10c-e16b646a937d"), getFrequency(EFrequency.WEEKLY.name()), getProcessingDateType(EProcessingDateType.CUSTOM.name()), false, true, Status.ACTIVE));

        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("b112c3ef-8338-48c5-ac1e-72fb1ab1734d"), getFrequency(EFrequency.MONTHLY.name()), getProcessingDateType(EProcessingDateType.FIRST_DAY_OF_THE_MONTH.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("3159cf37-e457-4482-ac29-87d7202ea485"), getFrequency(EFrequency.MONTHLY.name()), getProcessingDateType(EProcessingDateType.LAST_DAY_OF_THE_PREVIOUS_MONTH.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("86f91cb4-ba25-40d9-8e36-b03c79e90b06"), getFrequency(EFrequency.MONTHLY.name()), getProcessingDateType(EProcessingDateType.MONTH.name()), true, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("527c92cf-166d-4167-9c7d-008866f605bb"), getFrequency(EFrequency.MONTHLY.name()), getProcessingDateType(EProcessingDateType.CUSTOM.name()), false, true, Status.ACTIVE));

        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("470ca9d9-b26b-4d42-b9fe-e237d9b78d4c"), getFrequency(EFrequency.ANNUALLY.name()), getProcessingDateType(EProcessingDateType.FIRST_DAY_OF_THE_YEAR.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("ee4ea6bc-e89c-4c0a-b04b-521900e181f7"), getFrequency(EFrequency.ANNUALLY.name()), getProcessingDateType(EProcessingDateType.LAST_DAY_OF_THE_PREVIOUS_YEAR.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("303212d4-8c13-460a-b86b-a24838566aca"), getFrequency(EFrequency.ANNUALLY.name()), getProcessingDateType(EProcessingDateType.YEAR.name()), true, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("4eb43367-ceab-4021-bc59-792f1b1bbe61"), getFrequency(EFrequency.ANNUALLY.name()), getProcessingDateType(EProcessingDateType.CUSTOM.name()), false, true, Status.ACTIVE));

        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("99bf0b36-8bab-4323-828e-327129d18ddf"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.DAY.name()), true, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("ce556efa-5332-461f-ac27-c07ed9466826"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.CUSTOM.name()), false, true, Status.ACTIVE));

        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("d65219f9-0f12-405b-a9e6-08dc7b14930b"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.TODAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("9a45036d-33c2-40c7-95da-163d3e0208c1"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.YESTERDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("efdce856-b933-4d51-a7a6-fed06c92c221"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.BEFORE_YESTERDAY.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("ceac2071-defb-4a1f-ae8c-dc294dc37c76"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.FIRST_DAY_OF_WEEK.name()), false, false, Status.ACTIVE));
        createSchedulerProcessingRuleIfNotExists(new BusinessProcessSchedulerProcessingRuleDto(UUID.fromString("1db374b0-7d53-42d5-a120-77de711320c3"), getFrequency(EFrequency.ONE_TIME.name()), getProcessingDateType(EProcessingDateType.LAST_DAY_OF_THE_PREVIOUS_WEEK.name()), false, false, Status.ACTIVE));


    }

    private void createBusinesProcessIfNotExists(BusinessProcessDto businessProcess){
        try{
            businessProcessService.findById(businessProcess.getId());
        }catch (BusinessNotFoundException e){
            businessProcessService.create(businessProcess);
        }
    }

    private void createBusinesProcessSchedulerIfNotExists(BusinessProcessSchedulerDto businessProcessScheduler){
        try{
            businessProcessSchedulerService.findById(businessProcessScheduler.getId());
        }catch (BusinessNotFoundException e){
            businessProcessSchedulerService.create(businessProcessScheduler);
        }
    }

    private void createSchedulerRuleIfNotExists(BusinessProcessSchedulerExecutionRuleDto schedulerRule){
        try{
            businessProcessSchedulerRuleService.getById(schedulerRule.getId());
        } catch (BusinessNotFoundException e) {
            businessProcessSchedulerRuleService.create(schedulerRule);
        }
    }

    private void createSchedulerProcessingRuleIfNotExists(BusinessProcessSchedulerProcessingRuleDto schedulerProcessingRule){
        try{
            businessProcessSchedulerProcessingRuleService.getById(schedulerProcessingRule.getId());
        } catch (BusinessNotFoundException e) {
            businessProcessSchedulerProcessingRuleService.create(schedulerProcessingRule);
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
    private ProcessingDateTypeDto getProcessingDateType(String code){
        return processingDateTypeService.getByCode(code);
    }
}
