package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;

public interface IInvoiceReconcileAutomaticService {

    void reconcileAutomatic(InvoiceReconcileAutomaticRequest request);
    
}
