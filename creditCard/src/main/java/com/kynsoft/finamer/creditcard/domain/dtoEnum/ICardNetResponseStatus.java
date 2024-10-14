package com.kynsoft.finamer.creditcard.domain.dtoEnum;

public interface ICardNetResponseStatus {
    /**
     * Return the code of this CardNet response status.
     *
     * @return the string code associated with the response status
     */
    String value();

    /**
     * Return the series of this CardNet response status.
     *
     * @return the {@code Series} associated with the response status
     */
    ETransactionResultStatus transactionStatus();

    /**
     * Return the message or reason phrase associated with this CardNet response status.
     *
     * @return the reason phrase explaining the response status
     */
    String getMessage();
}