package com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile.InvoiceReconcileImportProcessStatusRedisRepository;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InvoiceReconcileImportProcessStatusQueryHandler implements IQueryHandler<InvoiceReconcileImportProcessStatusQuery,
        InvoiceReconcileImportProcessStatusResponse> {

    private final InvoiceReconcileImportService reconcileImportService;

    public InvoiceReconcileImportProcessStatusQueryHandler(InvoiceReconcileImportService reconcileImportService) {
        this.reconcileImportService = reconcileImportService;
    }

    @Override
    public InvoiceReconcileImportProcessStatusResponse handle(InvoiceReconcileImportProcessStatusQuery query) {
            return reconcileImportService.getReconcileImportProcessStatus(query.getRequest());
    }
}
