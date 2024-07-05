package com.kynsoft.finamer.settings.application.query.manageRoomCategory.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageRoomCategoryByIdQuery implements IQuery {
    private UUID id;
}
