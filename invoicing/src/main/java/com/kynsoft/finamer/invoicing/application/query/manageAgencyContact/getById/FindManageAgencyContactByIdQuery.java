package com.kynsoft.finamer.invoicing.application.query.manageAgencyContact.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageAgencyContactByIdQuery implements IQuery {
    private UUID id;
}
