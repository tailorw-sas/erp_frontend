package com.kynsoft.finamer.payment.application.services.payment.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.core.payment.ProcessUpdatePayment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class UpdatePaymentService {

    private final IManageClientService clientService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageBankAccountService bankAccountService;
    private final IPaymentService paymentService;

    public UpdatePaymentService(IManageClientService clientService,
                                IManageAgencyService agencyService,
                                IManageHotelService hotelService,
                                IManageBankAccountService bankAccountService,
                                IPaymentService paymentService){
        this.clientService = clientService;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.bankAccountService = bankAccountService;
        this.paymentService = paymentService;
    }

    @Transactional
    public void update(UUID paymentId,
                       UUID clientId,
                       UUID agencyId,
                       UUID hotelId,
                       UUID bankAccountId,
                       String remark){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(paymentId, "id", "Payment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(clientId, "Client", "Client ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(agencyId, "Agency", "Agency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(hotelId, "Hotel", "Hotel ID cannot be null."));

        PaymentDto paymentDto = this.paymentService.findById(paymentId);

        if (!paymentDto.getPaymentSource().getExpense())
        {
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(bankAccountId, "bankAccount", "Bank Account ID cannot be null."));
        }

        ManageClientDto clientDto = this.clientService.findById(clientId);
        ManageHotelDto hotelDto = this.hotelService.findById(hotelId);
        ManageAgencyDto agencyDto = this.agencyService.findById(agencyId);
        ManageBankAccountDto bankAccount = this.getBankAccount(bankAccountId, paymentDto.getPaymentSource());

        ProcessUpdatePayment updatePayment = new ProcessUpdatePayment(paymentDto, hotelDto, agencyDto, clientDto, bankAccount, remark);
        updatePayment.update();

        this.saveChanges(paymentDto);
    }
    
    private ManageBankAccountDto getBankAccount(UUID bankAccountId, ManagePaymentSourceDto paymentSource){
        try{
            if(Objects.nonNull(bankAccountId)){
                return this.bankAccountService.findById(bankAccountId);
            }else{
                if(!paymentSource.getExpense()){
                    throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND.getReasonPhrase())));
                }
                return null;
            }
        }catch (BusinessNotFoundException ex){
            if(!paymentSource.getExpense()){
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND.getReasonPhrase())));
            }else{
                return null;
            }
        }
    }

    private void saveChanges(PaymentDto payment){
        this.paymentService.update(payment);
    }
}
