package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.PaymentImportAntiIncomeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.PaymentImportBankHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.PaymentImportDetailHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.PaymentImportExpenseHelperServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportHelperProvider {

    private final PaymentImportAntiIncomeHelperServiceImpl antiIncomeHelperService;

    private final PaymentImportBankHelperServiceImpl bankHelperService;

    private final PaymentImportDetailHelperServiceImpl detailHelperService;

    private final PaymentImportExpenseHelperServiceImpl expenseHelperService;


    public PaymentImportHelperProvider(PaymentImportAntiIncomeHelperServiceImpl antiIncomeHelperService, PaymentImportBankHelperServiceImpl bankHelperService, PaymentImportDetailHelperServiceImpl detailHelperService, PaymentImportExpenseHelperServiceImpl expenseHelperService) {
        this.antiIncomeHelperService = antiIncomeHelperService;
        this.bankHelperService = bankHelperService;
        this.detailHelperService = detailHelperService;
        this.expenseHelperService = expenseHelperService;
    }


    public AbstractPaymentImportHelperService provideImportHelperService(EImportPaymentType importPaymentType){

        return switch (importPaymentType){
            case EXPENSE -> expenseHelperService;
            case BANK -> bankHelperService;
            case ANTI -> antiIncomeHelperService;
            case DETAIL -> detailHelperService;
        };
    }
}
