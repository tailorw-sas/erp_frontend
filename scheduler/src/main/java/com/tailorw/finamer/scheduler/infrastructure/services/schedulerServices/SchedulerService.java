package com.tailorw.finamer.scheduler.infrastructure.services.schedulerServices;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.finamer.scheduler.domain.dto.*;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessSchedulerService;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;
import com.tailorw.finamer.scheduler.domain.services.IFrecuencyService;
import com.tailorw.finamer.scheduler.domain.services.ISchedulerLogService;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.*;
import com.tailorw.finamer.scheduler.infrastructure.services.businessProcesses.TcaRoomRatesBusinessProcessServiceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SchedulerService {

    private final IBusinessProcessService businessProcessService;
    private final IBusinessProcessSchedulerService businessProcessSchedulerService;
    private final ISchedulerLogService logService;
    private final String businessProcessCode;
    private final IFrecuencyService frecuencyService;

    Logger logger = Logger.getLogger(TcaRoomRatesBusinessProcessServiceImpl.class.getName());

    public SchedulerService(IBusinessProcessService businessProcessService,
                            IBusinessProcessSchedulerService businessProcessSchedulerService,
                            final ISchedulerLogService logService,
                            String businessProcessCode,
                            IFrecuencyService frecuencyService){
        this.businessProcessService = businessProcessService;
        this.businessProcessSchedulerService = businessProcessSchedulerService;
        this.logService = logService;
        this.businessProcessCode = businessProcessCode;
        this.frecuencyService = frecuencyService;
    }

    protected abstract void processOneTime();

    protected abstract void processDaily();

    protected abstract void processWeekly();

    public void processOneTimeSchedulers(ISchedulerProcess process){
        BusinessProcessDto businessProcess = getBusinessProcess();
        if(!isBusinessProcessValid(businessProcess)){
            logInfo(String.format("BusinessProcess %s is not valid!", this.businessProcessCode));
            return;
        }

        FrequencyDto frequency = getFrequencyByCode(EFrequency.ONE_TIME);
        if(!isValidFrequency(frequency)){
            logInfo(String.format("Frequency %s is not valid!", EFrequency.ONE_TIME));
            return;
        }

        List<BusinessProcessSchedulerDto> oneTimeSchedulers = getSchedulersByBusinessProcessAndFrequency(businessProcess, frequency);
        oneTimeSchedulers.forEach(scheduler -> processSchedulerOneTime(process, scheduler));
    }

    public void processDailySchedulers(ISchedulerProcess process) {
        BusinessProcessDto businessProcess = getBusinessProcess();
        if(!isBusinessProcessValid(businessProcess)){
            logInfo(String.format("BusinessProcess %s is not valid!", this.businessProcessCode));
            return;
        }

        FrequencyDto frequency = getFrequencyByCode(EFrequency.DAILY);
        if(!isValidFrequency(frequency)){
            logInfo(String.format("Frequency %s is not valid!", EFrequency.DAILY));
            return;
        }

        List<BusinessProcessSchedulerDto> dailySchedulers = getSchedulersByBusinessProcessAndFrequency(businessProcess, frequency);

        dailySchedulers.forEach(scheduler -> processSchedulerDaily(process, scheduler));
    }

    public void processWeeklySchedulers(ISchedulerProcess process) {
        BusinessProcessDto businessProcess = getBusinessProcess();
        if(!isBusinessProcessValid(businessProcess)){
            logInfo(String.format("BusinessProcess %s is not valid!", this.businessProcessCode));
            return;
        }

        FrequencyDto frequency = getFrequencyByCode(EFrequency.WEEKLY);
        if(!isValidFrequency(frequency)){
            logInfo(String.format("Frequency %s is not valid!", EFrequency.WEEKLY));
            return;
        }

        List<BusinessProcessSchedulerDto> weeklySchedulers = getSchedulersByBusinessProcessAndFrequency(businessProcess, frequency);

        weeklySchedulers.forEach(scheduler -> processSchedulerWeekly(process, scheduler));

    }

    public void processMonthlySchedulers(ISchedulerProcess process) {

    }

    public void processAnnuallySchedulers(ISchedulerProcess process) {

    }

    private void processSchedulerOneTime(ISchedulerProcess process, BusinessProcessSchedulerDto scheduler){
        if(!mustBeExecuteOneTime(scheduler)){
            return;
        }
        processScheduler(scheduler, process);
    }

    private void processSchedulerDaily(ISchedulerProcess process, BusinessProcessSchedulerDto scheduler) {
        if (!mustBeExecutedDaily(scheduler)) {
            return;
        }
        processScheduler(scheduler, process);
    }

    private void processSchedulerWeekly(ISchedulerProcess process, BusinessProcessSchedulerDto scheduler) {
        if (!mustBeExecutedWeekly(scheduler)) {
            return;
        }
        processScheduler(scheduler, process);
    }

    private boolean mustBeExecuteOneTime(BusinessProcessSchedulerDto scheduler){
        if (scheduler == null || scheduler.getIntervalType() == null) {
            throw new IllegalArgumentException("Scheduler or its interval type cannot be null");
        }

        if (scheduler.getLastExecutionDatetime() == null) {
            scheduler.setLastExecutionDatetime(LocalDate.now().atStartOfDay().minusMinutes(1));
        }

        LocalDate today = LocalDate.now();

        if(!today.isEqual(scheduler.getExecutionDate())){
            return false;
        }

        return mustBeExecuteNow(scheduler);
    }

    private boolean mustBeExecutedDaily(BusinessProcessSchedulerDto scheduler) {
        if (scheduler == null || scheduler.getIntervalType() == null) {
            throw new IllegalArgumentException("Scheduler or its interval type cannot be null");
        }

        if (scheduler.getLastExecutionDatetime() == null) {
            scheduler.setLastExecutionDatetime(LocalDate.now().atStartOfDay().minusMinutes(1));
        }

        return mustBeExecuteNow(scheduler);
    }

    private boolean mustBeExecutedWeekly(BusinessProcessSchedulerDto scheduler) {
        if (scheduler == null || scheduler.getIntervalType() == null) {
            throw new IllegalArgumentException("Scheduler or its interval type cannot be null");
        }

        if (scheduler.getLastExecutionDatetime() == null) {
            scheduler.setLastExecutionDatetime(LocalDate.now().atStartOfDay().minusWeeks(1));
        }

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        if(!LocalDate.now().getDayOfWeek().toString().equals(scheduler.getExecutionDateType().toString())){
            return false;
        }

        return mustBeExecuteNow(scheduler);
    }

    private boolean mustBeExecuteNow(BusinessProcessSchedulerDto scheduler){
        if (scheduler.isInProcess()) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastExecution = scheduler.getLastExecutionDatetime();
        boolean isNewDay = now.toLocalDate().isAfter(lastExecution.toLocalDate());

        EIntervalType intervalTypeEnum = getIntervalTypeEnum(scheduler.getIntervalType().getCode());
        if(Objects.isNull(intervalTypeEnum)){
            return false;
        }

        return switch (intervalTypeEnum) {
            case MINUTE -> {
                // Si es un nuevo día o ha pasado el intervalo en minutos y esta dentro del rango de hora de inicio y fin establecido
                LocalDateTime nextExecutionTime = lastExecution.plusMinutes(scheduler.getInterval());
                yield (isNewDay && this.isBetweenStartAndEndTime(now, scheduler)) || (now.isAfter(nextExecutionTime) && this.isBetweenStartAndEndTime(now, scheduler));
            }
            case HOUR -> {
                // Si es un nuevo día o ha pasado el intervalo en horas y esta dentro del rango de hora de inicio y fin establecido
                LocalDateTime nextExecutionTime = lastExecution.plusHours(scheduler.getInterval());
                yield (isNewDay && this.isBetweenStartAndEndTime(now, scheduler)) || (now.isAfter(nextExecutionTime) && this.isBetweenStartAndEndTime(now, scheduler));
            }
            case ONE_TIME ->
                // Ejecutar solo una vez en el día y a partir de la hora especificada
                    isNewDay && now.toLocalTime().isAfter(scheduler.getExecutionTime());
            default -> false;
        };
    }

    private LocalDate getProcessingDate(BusinessProcessSchedulerDto scheduler) {
        LocalDate now = LocalDate.now();

        EProcessingDateType processingDateTypeEnum = getProcessingDateTypeEnum(scheduler.getProcessingDateType().getCode());
        if(Objects.isNull(processingDateTypeEnum)){
            return null;
        }

        return switch (processingDateTypeEnum) {
            case TODAY -> now;
            case YESTERDAY -> now.minusDays(1);
            case BEFORE_YESTERDAY -> now.minusDays(2);
            case FIRST_DAY_OF_WEEK -> now.with(DayOfWeek.MONDAY);
            case FIRST_DAY_OF_THE_MONTH -> now.withDayOfMonth(1);
            case FIRST_DAY_OF_THE_YEAR -> now.withDayOfYear(1);
            case LAST_DAY_OF_THE_PREVIOUS_WEEK -> now.with(DayOfWeek.SUNDAY).minusWeeks(1);
            case LAST_DAY_OF_THE_PREVIOUS_MONTH ->
                    now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
            case LAST_DAY_OF_THE_PREVIOUS_YEAR -> now.minusYears(1).withDayOfYear(now.minusYears(1).lengthOfYear());
            case LAST_MONDAY -> now.with(DayOfWeek.MONDAY).minusWeeks(1);
            case LAST_TUESDAY -> now.with(DayOfWeek.TUESDAY).minusWeeks(1);
            case LAST_WEDNESDAY -> now.with(DayOfWeek.WEDNESDAY).minusWeeks(1);
            case LAST_THURSDAY -> now.with(DayOfWeek.THURSDAY).minusWeeks(1);
            case LAST_FRIDAY -> now.with(DayOfWeek.FRIDAY).minusWeeks(1);
            case LAST_SATURDAY -> now.with(DayOfWeek.SATURDAY).minusWeeks(1);
            case LAST_SUNDAY -> now.with(DayOfWeek.SUNDAY).minusWeeks(1);
            case LAST_MONTH -> now.minusMonths(1);
            case LAST_WEEK -> now.minusWeeks(1);
            case LAST_YEAR -> now.minusYears(1);
            case DAY -> now.minusDays(scheduler.getProcessingDateValue());
            case MONTH -> now.minusMonths(scheduler.getProcessingDateValue());
            case YEAR -> now.minusYears(scheduler.getProcessingDateValue());
            case CUSTOM -> scheduler.getProcessingDate();
            default ->
                    throw new IllegalArgumentException("Invalid ProcessingDateType: " + scheduler.getProcessingDateType());
        };
    }

    private void processScheduler(BusinessProcessSchedulerDto scheduler, ISchedulerProcess process){
        SchedulerLogDto schedulerLog = createInitialLog(scheduler);
        try {
            LocalDate processingDate = getProcessingDate(scheduler);

            scheduler.setInProcess(true);
            businessProcessSchedulerService.update(scheduler);

            schedulerLog.setProcessingDate(processingDate);
            setSchedulerLogAsInProcess(schedulerLog);

            process.executeProcess(scheduler, schedulerLog, processingDate);
        } catch (Exception ex) {
            logError("Error executing process", ex);
            schedulerLog.setDetails(ex.toString());
        } finally {
            if(scheduler.isAllowsQueueing()){
                updateSchedulerAfterExecution(scheduler);
                setSchedulerLogACompleted(schedulerLog);
            }
        }
    }


    private boolean isBusinessProcessValid(BusinessProcessDto businessProcess){
        if (businessProcess == null) {
            logInfo(String.format("Business Process %s not found", businessProcessCode));
            return false;
        }

        if (isProcessInactiveOrDeleted(businessProcess)) {
            logInfo(String.format("Business Process %s is inactive or deleted", businessProcess.getProcessName()));
            return false;
        }

        return true;
    }

    private BusinessProcessDto getBusinessProcess(){
        BusinessProcessDto businessProcess = businessProcessService.findByCode(businessProcessCode);
        if(businessProcess != null){
            return businessProcess;
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_PROCESS_NOT_FOUND, new ErrorField("businessProcessCode", DomainErrorMessage.BUSINESS_PROCESS_NOT_FOUND.getReasonPhrase())));
    }

    private boolean isValidFrequency(FrequencyDto frequency){
        return !Objects.isNull(frequency);
    }

    private EIntervalType getIntervalTypeEnum(String code){
        if(EIntervalType.exists(code)){
            return EIntervalType.getCode(code);
        }
        return null;
    }

    private EProcessingDateType getProcessingDateTypeEnum(String code){
        if(EProcessingDateType.exists(code)){
            return EProcessingDateType.getCode(code);
        }
        return null;
    }

    private List<BusinessProcessSchedulerDto> getSchedulersByBusinessProcessAndFrequency(BusinessProcessDto businessProcess, FrequencyDto frequency){
        return businessProcessSchedulerService.findByProcessIdAndStatusAndFrequency(businessProcess.getId(), Status.ACTIVE, frequency.getId());
    }

    private boolean isProcessInactiveOrDeleted(BusinessProcessDto businessProcessDto) {
        return Status.INACTIVE.equals(businessProcessDto.getStatus()) ||
                Status.DELETED.equals(businessProcessDto.getStatus());
    }

    private FrequencyDto getFrequencyByCode(EFrequency frequencyEnum){
        return frecuencyService.getByCode(frequencyEnum.name());
    }

    private void updateSchedulerAfterExecution(BusinessProcessSchedulerDto scheduler) {
        scheduler.setInProcess(false);
        scheduler.setLastExecutionDatetime(LocalDateTime.now());
        businessProcessSchedulerService.update(scheduler);
    }

    private void setSchedulerLogACompleted(SchedulerLogDto schedulerLog){
        schedulerLog.setStatus(ProcessStatus.COMPLETED);
        schedulerLog.setCompletedAt(LocalDateTime.now());
        logService.update(schedulerLog);
    }

    private SchedulerLogDto createInitialLog(BusinessProcessSchedulerDto scheduler){
        SchedulerLogDto schedulerLog = new SchedulerLogDto(
                UUID.randomUUID(),
                scheduler,
                LocalDate.now(),
                ProcessStatus.INITIATED,
                null,
                null,
                scheduler.getProcessingDate(),
                "");
        UUID id = logService.create(schedulerLog);
        schedulerLog.setId(id);
        return schedulerLog;
    }

    private void setSchedulerLogAsInProcess(SchedulerLogDto schedulerLog){
        schedulerLog.setStatus(ProcessStatus.IN_PROCESS);
        logService.update(schedulerLog);
    }

    private boolean isBetweenStartAndEndTime(LocalDateTime now, BusinessProcessSchedulerDto businessProcessSchedulerDto){
        LocalTime startTime = Objects.isNull(businessProcessSchedulerDto.getStartTime()) ? LocalTime.MIDNIGHT : businessProcessSchedulerDto.getStartTime();
        LocalTime endTime = Objects.isNull(businessProcessSchedulerDto.getEndTime()) ? LocalTime.of(23, 59, 59) : businessProcessSchedulerDto.getEndTime();
        LocalTime currentTime = now.toLocalTime();

        return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    private void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    private void logError(String message, Exception ex) {
        logger.log(Level.SEVERE, message, ex);
    }
}
