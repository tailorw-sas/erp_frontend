package com.tailorw.finamer.scheduler.domain.services;

public interface ISchedulerService {

    void processDailySchedulers();

    void processWeeklySchedulers();

    void processMonthlySchedulers();

    void processAnnuallySchedulers();
}
