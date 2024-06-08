package com.kynsoft.finamer.settings.application.query.manageTradingCompanies.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageTradingCompaniesByIdQuery implements IQuery {

    private final UUID id;
}
