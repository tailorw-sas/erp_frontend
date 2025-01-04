package com.kynsoft.finamer.invoicing.application.query.collections.invoice;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceCollectionsSummaryQueryHandler implements IQueryHandler<InvoiceCollectionsSummaryQuery, InvoiceCollectionResponse> {

    private final IManageInvoiceService manageInvoiceService;

    public InvoiceCollectionsSummaryQueryHandler(IManageInvoiceService manageInvoiceService) {
        this.manageInvoiceService = manageInvoiceService;
    }

    @Override
    public InvoiceCollectionResponse handle(InvoiceCollectionsSummaryQuery query) {

        Page<ManageInvoiceDto> invoiceForSummary = manageInvoiceService.getInvoiceForSummary(query.getPageable(),query.getFilter());
        long totalInvoiceWith30=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==30)
                .count();
        long totalInvoiceWith60=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==60)
                .count();
        long totalInvoiceWith90=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==90)
                .count();
        long totalInvoiceWith120=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==120)
                .count();
        long totalInvoiceWithAging0=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==0)
                .count();
        long percentAgingWith30= !invoiceForSummary.getContent().isEmpty() ?totalInvoiceWith30*100/invoiceForSummary.getContent().size():0;
        long percentAgingWith60=!invoiceForSummary.getContent().isEmpty() ?totalInvoiceWith60*100/invoiceForSummary.getContent().size():0;
        long percentAgingWith90=!invoiceForSummary.getContent().isEmpty() ?totalInvoiceWith90*100/invoiceForSummary.getContent().size():0;
        long percentAgingWith120=!invoiceForSummary.getContent().isEmpty() ?totalInvoiceWith120*100/invoiceForSummary.getContent().size():0;

        double balanceInvoiceWithAging30=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==30)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging60=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==60)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging90=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==90)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging120=invoiceForSummary.getContent().parallelStream().filter(invoice->invoice.getAging()==120)
                .mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double totalInvoiceAmount=invoiceForSummary.getContent().parallelStream().mapToDouble(ManageInvoiceDto::getInvoiceAmount).sum();
        double totalInvoiceDue=invoiceForSummary.getContent().parallelStream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();


    InvoiceCollectionsSummaryResponse summaryResponse= InvoiceCollectionsSummaryResponse.builder()
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
        PaginatedResponse paginatedResponse = new PaginatedResponse(invoiceForSummary.getContent()
                ,invoiceForSummary.getTotalPages(),
                invoiceForSummary.getNumberOfElements(),
                invoiceForSummary.getTotalElements()
                ,invoiceForSummary.getSize(),
                invoiceForSummary.getNumber());
        return new InvoiceCollectionResponse(paginatedResponse,summaryResponse);
    }
}
