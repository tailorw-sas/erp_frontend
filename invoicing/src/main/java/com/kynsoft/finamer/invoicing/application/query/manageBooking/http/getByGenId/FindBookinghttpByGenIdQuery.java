package com.kynsoft.finamer.invoicing.application.query.manageBooking.http.getByGenId;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindBookinghttpByGenIdQuery  implements IQuery {

    private final Long id;

    public FindBookinghttpByGenIdQuery(Long id) {
        this.id = id;
    }

}
