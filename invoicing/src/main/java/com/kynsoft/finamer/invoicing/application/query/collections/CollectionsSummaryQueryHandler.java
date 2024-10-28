package com.kynsoft.finamer.invoicing.application.query.collections;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
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
        long totalInvoiceWith30=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==30)
                .count();
        long totalInvoiceWith60=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==60)
                .count();
        long totalInvoiceWith90=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==90)
                .count();
        long totalInvoiceWith120=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==120)
                .count();
        long totalInvoiceWithAging0=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==0)
                .count();
        long percentAgingWith30=totalInvoiceWith30*100/invoiceForSummary.size();
        long percentAgingWith60=totalInvoiceWith60*100/invoiceForSummary.size();
        long percentAgingWith90=totalInvoiceWith90*100/invoiceForSummary.size();
        long percentAgingWith120=totalInvoiceWith120*100/invoiceForSummary.size();

        double balanceInvoiceWithAging30=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==30)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging60=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==60)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging90=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==90)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging120=invoiceForSummary.parallelStream().filter(invoice->invoice.getAging()==120)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double totalInvoiceAmount=invoiceForSummary.parallelStream().mapToDouble(ManageInvoiceDto::getInvoiceAmount).sum();
        double totalInvoiceDue=invoiceForSummary.parallelStream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();


    return CollectionsSummaryResponse.builder()
             .totalInvoiceWith30(totalInvoiceWith30)
             .totalInvoiceWithAging90(totalInvoiceWith90)
             .totalInvoiceWithAging60(totalInvoiceWith60)
             .totalInvoiceWithAging120(totalInvoiceWith120)
             .totalInvoiceWithAging0(totalInvoiceWithAging0)
             .totalInvoicePercentWithAging30(percentAgingWith30)
             .totalInvoicePercentWithAging60(percentAgingWith60)
             .totalInvoicePercentWithAging90(percentAgingWith90)
             .totalInvoicePercentWithAging120(percentAgingWith120)
             .totalInvoiceBalanceWithAging30(balanceInvoiceWithAging30)
             .totalInvoiceBalanceWithAging60(balanceInvoiceWithAging60)
             .totalInvoiceBalanceWithAging90(balanceInvoiceWithAging90)
             .totalInvoiceBalanceWithAging60(balanceInvoiceWithAging60)
             .totalInvoiceBalanceWithAging120(balanceInvoiceWithAging120)
             .totalInvoiceWithAging0(totalInvoiceWithAging0)
             .totalInvoiceAmount(totalInvoiceAmount)
             .totalInvoiceDueAmount(totalInvoiceDue)
             .build();

    }
}
