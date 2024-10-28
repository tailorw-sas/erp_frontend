package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CollectionSummaryDto {
    private int totalInvoiceWith30;
    private double totalInvoiceBalanceWithAging30;
    private int totalInvoicePercentWithAging30;
    private int totalInvoiceWithAging60;
    private double totalInvoiceBalanceWithAging60;
    private int totalInvoicePercentWithAging60;
    private int totalInvoiceWithAging90;
    private double totalInvoiceBalanceWithAging90;
    private int totalInvoicePercentWithAging90;
    private int totalInvoiceWithAging120;
    private double totalInvoiceBalanceWithAging120;
    private int totalInvoicePercentWithAging120;
    private int totalInvoiceWithAging0;
    private double totalInvoiceAmount;
}
