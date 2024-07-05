package com.kynsoft.finamer.settings.application.query.manageReport.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageReportByIdQuery implements IQuery {

    private final UUID id;
}
