package com.kynsoft.finamer.invoicing.application.query.collections;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CollectionsSummaryResponse implements IResponse {
    private long totalInvoiceWith30;
    private double totalInvoiceBalanceWithAging30;
    private long totalInvoicePercentWithAging30;
    private long totalInvoiceWithAging60;
    private double totalInvoiceBalanceWithAging60;
    private long totalInvoicePercentWithAging60;
    private long totalInvoiceWithAging90;
    private double totalInvoiceBalanceWithAging90;
    private long totalInvoicePercentWithAging90;
    private long totalInvoiceWithAging120;
    private double totalInvoiceBalanceWithAging120;
    private long totalInvoicePercentWithAging120;
    private long totalInvoiceWithAging0;
    private double totalInvoiceAmount;
    private double totalInvoiceDueAmount;
}
