package com.kynsoft.finamer.payment.application.query.payment.excelExporter;

import com.kynsof.share.core.domain.request.SearchRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchExcelExporter {

    private SearchRequest search;
    private String fileName;
}
