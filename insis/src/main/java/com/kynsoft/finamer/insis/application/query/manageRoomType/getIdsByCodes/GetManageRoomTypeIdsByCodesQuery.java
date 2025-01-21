package com.kynsoft.finamer.insis.application.query.manageRoomType.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsoft.finamer.insis.application.command.manageRoomType.create.CreateRoomTypeCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetManageRoomTypeIdsByCodesQuery implements IQuery {

    public UUID hotel;
    public List<String> codes;
}
