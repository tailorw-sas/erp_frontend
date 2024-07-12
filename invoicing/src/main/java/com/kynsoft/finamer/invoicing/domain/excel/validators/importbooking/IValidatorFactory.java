package com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking;

public interface IValidatorFactory<T> {

    void createValidators();

    boolean validate(T toValidate);
}
