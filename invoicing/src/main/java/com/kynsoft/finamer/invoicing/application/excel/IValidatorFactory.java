package com.kynsoft.finamer.invoicing.application.excel;

public interface IValidatorFactory<T> {

    void createValidators();

    boolean validate(T toValidate);
}
