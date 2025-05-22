package com.kynsoft.finamer.payment.domain.core.payment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.rules.payment.PaymentValidateBankAccountAndHotelRule;


public class ProcessUpdatePayment {

    private final PaymentDto payment;
    private final ManageHotelDto hotel;
    private final ManageAgencyDto agency;
    private final ManageClientDto client;
    private final ManageBankAccountDto bankAccount;
    private final String remark;


    public ProcessUpdatePayment(PaymentDto payment,
                                ManageHotelDto hotel,
                                ManageAgencyDto agency,
                                ManageClientDto client,
                                ManageBankAccountDto bankAccount,
                                String remark){
        this.payment = payment;
        this.hotel = hotel;
        this.agency = agency;
        this.client = client;
        this.bankAccount = bankAccount;
        this.remark = remark;
    }

    public void update(){
        this.updatePayment(this.payment,
                this.hotel,
                this.agency,
                this.client,
                this.remark);
    }

    private void updatePayment(PaymentDto payment,
                               ManageHotelDto hotel,
                               ManageAgencyDto agency,
                               ManageClientDto client,
                               String remark
                               ){
        payment.setHotel(hotel);
        payment.setAgency(agency);
        payment.setClient(client);
        payment.setRemark(remark);

        if (!payment.getPaymentSource().getExpense())
        {
            if (payment.getBankAccount() == null) {
                payment.setBankAccount(this.bankAccount);
            } else if(!bankAccount.getId().equals(payment.getBankAccount().getId())) {
                payment.setBankAccount(this.bankAccount);
                RulesChecker.checkRule(new PaymentValidateBankAccountAndHotelRule(payment.getHotel(), payment.getBankAccount()));
            }
        } else {
            if (this.bankAccount == null) {
                payment.setBankAccount(null);
            } else if(payment.getBankAccount() == null) {
                payment.setBankAccount(this.bankAccount);
                RulesChecker.checkRule(new PaymentValidateBankAccountAndHotelRule(payment.getHotel(), payment.getBankAccount()));
            } else if(!this.bankAccount.getId().equals(payment.getBankAccount().getId())) {
                payment.setBankAccount(this.bankAccount);
                RulesChecker.checkRule(new PaymentValidateBankAccountAndHotelRule(payment.getHotel(), payment.getBankAccount()));
            }
        }
    }
}
