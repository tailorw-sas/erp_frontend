package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto;

import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ReconcileAutomaticValidatorFactory extends ValidatorFactory<InvoiceReconcileAutomaticRow> {

    private ReconcileAutomaticNightTypeValidator nightTypeValidator;
    private ReconcileAutomaticCouponNumberValidator couponNumberValidator;

    private ReconcileValidatorReservationNumberValidator reservationNumberValidator;

    private IManageBookingService manageBookingService;

    protected ReconcileAutomaticValidatorFactory(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public void createValidators(String importType) {
            nightTypeValidator = new ReconcileAutomaticNightTypeValidator();
            couponNumberValidator = new ReconcileAutomaticCouponNumberValidator(manageBookingService);
            reservationNumberValidator = new ReconcileValidatorReservationNumberValidator(manageBookingService);
    }

    @Override
    public boolean validate(InvoiceReconcileAutomaticRow toValidate) {
       if (reservationNumberValidator.validate(toValidate,errorFieldList)){
           return  couponNumberValidator.validate(toValidate,errorFieldList) &&
           nightTypeValidator.validate(toValidate,errorFieldList);
       }

        return false;
    }
}
