package com.kynsoft.finamer.invoicing.application.query.collections;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.dto.CollectionSummaryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectionsSummaryQueryHandler implements IQueryHandler<CollectionsSummaryQuery, CollectionsSummaryResponse> {

    private final IManageInvoiceService manageInvoiceService;

    public CollectionsSummaryQueryHandler(IManageInvoiceService manageInvoiceService) {
        this.manageInvoiceService = manageInvoiceService;
    }

    @Override
    public CollectionsSummaryResponse handle(CollectionsSummaryQuery query) {

        List<ManageInvoiceDto> invoiceForSummary = manageInvoiceService.getInvoiceForSummary(query.getFilter());

     CollectionSummaryDto.builder()
             .totalInvoiceBalanceWithAging30(invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()<=30)
                .count())
             .totalInvoiceBalanceWithAging90()
             .totalInvoiceBalanceWithAging60()
             .totalInvoiceBalanceWithAging120()
             .totalInvoiceWithAging0();


        return null;
    }
}
