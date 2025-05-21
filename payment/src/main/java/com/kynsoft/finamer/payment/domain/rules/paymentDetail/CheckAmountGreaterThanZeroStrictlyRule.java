package com.kynsoft.finamer.payment.domain.rules.paymentDetail;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;

public class CheckAmountGreaterThanZeroStrictlyRule extends BusinessRule {

    private final ManagePaymentSourceDto paymentSourceDto;
    private final boolean isFromCredit;
    private final Double amount;
    private final Double paymentBalance;

    public CheckAmountGreaterThanZeroStrictlyRule(ManagePaymentSourceDto paymentSourceDto, boolean isFromCredit, Double amount, Double paymentBalance) {
        super(DomainErrorMessage.CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY, new ErrorField("amount", DomainErrorMessage.CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY.getReasonPhrase()));
        this.paymentSourceDto = paymentSourceDto;
        this.isFromCredit = isFromCredit;
        this.amount = amount;
        this.paymentBalance = paymentBalance;
    }

    /**
     * Para esta regla aplicada. Si tenemos en cuenta que al crear un Payment inicialmente el valor de Payment Balance debe de estar en cero
     * cuando se desea crear un Payment Detail, la segunda parte de esta regla (menor o igual que el valor del campo Payment Balance.) Entra
     * en contradiccion con el hecho que el AMOUNT debe ser mayor que cero. Voy a quitar la segunda parte de la regla para continuar trabajando y me
     * permita agregar Payment Detail.
     * LO ANTERIOR FUE UN ANALISIS CON LA QA, de lo cual se dedujo que Payment Balance debe de tomar el valor del Payment Amount al momento de crear el
     * New Payment.
     */
    /**
     * Amount: Si se selecciona un tipo de transacción con el check Cash marcado en el Manage Payment
     * Transaction Type, debe ingresarse un valor mayor estricto que 0 y menor o igual que el valor del
     * campo Payment Balance.
     * @return true or false
     */
    @Override
    public boolean isBroken() {
        if(paymentSourceDto.getExpense() && isFromCredit){
            return false;
        }
        return this.amount <= 0 || this.amount > this.paymentBalance;
    }

}
