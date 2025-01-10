package com.tailorw.finamer.scheduler.domain.services;

import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;

import java.util.UUID;

public interface ISchedulerLogService {
    UUID create(SchedulerLogDto dto);

    void update(SchedulerLogDto dto);

    SchedulerLogDto getById(UUID id);
}
