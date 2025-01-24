package com.kynsoft.finamer.insis.application.query.manageRoomCategory.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetManageRoomCategoryIdsByCodesQuery implements IQuery {

    private List<String> codes;
}
