package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.event.importError.CreateImportReconcileAutomaticErrorEvent;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ReconcileAutomaticValidatorFactory extends ValidatorFactory<InvoiceReconcileAutomaticRow> {

    private ReconcileAutomaticNightTypeValidator nightTypeValidator;
    private ReconcileAutomaticCouponNumberValidator couponNumberValidator;

    private ReconcileValidatorReservationNumberValidator reservationNumberValidator;

    private ReconcileAutomaticPriceValidator priceValidator;
    private final IManageBookingService manageBookingService;
    private final IManageNightTypeService nightTypeService;

    private String[] selectedInvoiceId;

    protected ReconcileAutomaticValidatorFactory(ApplicationEventPublisher applicationEventPublisher, IManageBookingService manageBookingService, IManageNightTypeService nightTypeService) {
        super(applicationEventPublisher);
        this.manageBookingService = manageBookingService;
        this.nightTypeService = nightTypeService;
    }

    @Override
    public void createValidators(String importType) {
        nightTypeValidator = new ReconcileAutomaticNightTypeValidator(nightTypeService);
        couponNumberValidator = new ReconcileAutomaticCouponNumberValidator(manageBookingService, selectedInvoiceId);
        reservationNumberValidator = new ReconcileValidatorReservationNumberValidator(manageBookingService, selectedInvoiceId);
        priceValidator = new ReconcileAutomaticPriceValidator(manageBookingService);

    }

    public void createValidators(String[] selectedInvoiceIds) {
        this.selectedInvoiceId = selectedInvoiceIds;
        this.createValidators("");
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow toValidate) {
        if (reservationNumberValidator.validate(toValidate, errorFieldList)) {
            couponNumberValidator.validate(toValidate, errorFieldList);
            nightTypeValidator.validate(toValidate, errorFieldList);
            priceValidator.validate(toValidate, errorFieldList);
        }

        this.sendErrorEvents(toValidate);
        boolean result = errorFieldList.isEmpty();
        this.clearErrors();
        return result;
    }


    private void sendErrorEvents(InvoiceReconcileAutomaticRow toValidate) {
        if (!errorFieldList.isEmpty()) {
            CreateImportReconcileAutomaticErrorEvent errorEvent = new CreateImportReconcileAutomaticErrorEvent(this,
                    InvoiceReconcileAutomaticImportErrorEntity.builder()
                            .errorFields(errorFieldList)
                            .importProcessId(toValidate.getImportProcessId())
                            .rowNumber(toValidate.getRowNumber())
                            .row(toValidate)
                            .invoiceId(toValidate.getInvoiceIds())
                            .build());
            applicationEventPublisher.publishEvent(errorEvent);
        }
    }
}
