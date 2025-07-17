package com.tailorw.tcaInnsist.application.query.rate.searchRateBetweenInvoiceDate;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchRateBetweenInvoiceDateQuery implements IQuery {

    private UUID processId;
    private String hotel;
    private String toInvoiceDate;
    private String fromInvoiceDate;
}
