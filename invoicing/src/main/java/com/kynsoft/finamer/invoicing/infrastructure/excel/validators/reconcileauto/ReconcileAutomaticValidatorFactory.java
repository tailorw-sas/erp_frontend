package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.event.importError.CreateImportReconcileAutomaticErrorEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ReconcileAutomaticValidatorFactory {

    private final ReconcileAutomaticInvoiceValidator reconcileAutomaticInvoiceValidator;
    private final ApplicationEventPublisher applicationEventPublisher;

    protected ReconcileAutomaticValidatorFactory(ReconcileAutomaticInvoiceValidator reconcileAutomaticInvoiceValidator, ApplicationEventPublisher applicationEventPublisher1) {
        this.reconcileAutomaticInvoiceValidator = reconcileAutomaticInvoiceValidator;
        this.applicationEventPublisher = applicationEventPublisher1;
    }

    public void prepareValidator(byte [] fileContent) throws IOException {
        reconcileAutomaticInvoiceValidator.loadWorkbook(fileContent);
    }

    public boolean validate(ManageInvoiceDto manageInvoiceDto,String importProcessId) {
        List<ErrorField> errorFieldList = reconcileAutomaticInvoiceValidator.validateInvoice(manageInvoiceDto);
        this.sendErrorEvents(manageInvoiceDto.getId().toString(),importProcessId,errorFieldList);
        return errorFieldList.isEmpty();
    }


    private void sendErrorEvents(String invoiceId,String importProcessId,List<ErrorField>  errorFieldList) {
        if (!errorFieldList.isEmpty()) {
            CreateImportReconcileAutomaticErrorEvent errorEvent = new CreateImportReconcileAutomaticErrorEvent(this,
                    InvoiceReconcileAutomaticImportErrorEntity.builder()
                            .errorFields(errorFieldList)
                            .importProcessId(importProcessId)
                            .invoiceId(invoiceId)
                            .build());
            applicationEventPublisher.publishEvent(errorEvent);
        }
    }

    public void releaseResources() throws IOException {
        reconcileAutomaticInvoiceValidator.closeWorkbook();;
    }
}
