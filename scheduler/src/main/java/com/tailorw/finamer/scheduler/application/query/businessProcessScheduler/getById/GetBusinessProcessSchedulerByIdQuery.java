package com.tailorw.finamer.scheduler.application.query.businessProcessScheduler.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetBusinessProcessSchedulerByIdQuery implements IQuery {
    private UUID id;
}
