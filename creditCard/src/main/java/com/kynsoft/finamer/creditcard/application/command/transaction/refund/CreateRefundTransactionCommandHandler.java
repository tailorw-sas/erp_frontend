package com.kynsoft.finamer.creditcard.application.command.transaction.refund;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.refundTransaction.RefundTransactionCompareParentAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateRefundTransactionCommandHandler implements ICommandHandler<CreateRefundTransactionCommand> {

    private final ITransactionService service;

    private final IParameterizationService parameterizationService;

    private final IManageTransactionStatusService transactionStatusService;

    private final IManageMerchantCommissionService manageMerchantCommissionService;

    private final ITransactionStatusHistoryService transactionStatusHistoryService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    public CreateRefundTransactionCommandHandler(ITransactionService service, IParameterizationService parameterizationService, IManageTransactionStatusService transactionStatusService, IManageMerchantCommissionService manageMerchantCommissionService, ITransactionStatusHistoryService transactionStatusHistoryService, IManageVCCTransactionTypeService transactionTypeService) {
        this.service = service;
        this.parameterizationService = parameterizationService;
        this.transactionStatusService = transactionStatusService;
        this.manageMerchantCommissionService = manageMerchantCommissionService;
        this.transactionStatusHistoryService = transactionStatusHistoryService;
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public void handle(CreateRefundTransactionCommand command) {
        RulesChecker.checkRule(new RefundTransactionCompareParentAmountRule(this.service, command.getParentId(), command.getAmount()));
        TransactionDto parentTransaction = this.service.findById(command.getParentId());
        if(!parentTransaction.getPermitRefund()){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_CANNOT_BE_REFUNDED, new ErrorField("refund", DomainErrorMessage.VCC_TRANSACTION_CANNOT_BE_REFUNDED.getReasonPhrase())));
        }

        double amount = command.getAmount();
        double commission = 0.0;
        double netAmount = command.getAmount();
        if(command.getHasCommission()
                && parentTransaction.getMerchant() != null
                && parentTransaction.getCreditCardType() != null){

            UUID parentMerchantId = parentTransaction.getMerchant().getId();
            UUID parentCreditCardTypeId = parentTransaction.getCreditCardType().getId();
            LocalDateTime parentCheckIn = parentTransaction.getCheckIn();

            ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();

            //si no encuentra la parametrization que agarre 2 decimales por defecto
            int decimals = parameterizationDto != null ? parameterizationDto.getDecimals() : 2;

            commission = manageMerchantCommissionService.calculateCommission(command.getAmount(), parentMerchantId, parentCreditCardTypeId, parentCheckIn.toLocalDate(), decimals);
            //independientemente del valor de la commission el netAmount tiene dos decimales
            netAmount = BankerRounding.round(command.getAmount() - commission, 2);
        }

        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByETransactionStatus(ETransactionStatus.REFUND);
        double parentAmountAndCommandAmount =  service.findSumOfAmountByParentId(parentTransaction.getId()) + command.getAmount();
        ManageVCCTransactionTypeDto transactionTypeDto = this.transactionTypeService.findByRefund();
//        if (transactionTypeDto != null && transactionTypeDto.getNegative()){
//            amount = -command.getAmount();
//            commission = -commission;
//            netAmount = -netAmount;
//        }
        TransactionDto transactionDto = this.service.create(new TransactionDto(
                UUID.randomUUID(),
                parentTransaction.getMerchant(),
                parentTransaction.getMethodType(),
                parentTransaction.getHotel(),
                parentTransaction.getAgency(),
                parentTransaction.getLanguage(),
                amount,
                parentTransaction.getCheckIn(),
                parentTransaction.getReservationNumber(),
                parentTransaction.getReferenceNumber(),
                parentTransaction.getHotelContactEmail(),
                parentTransaction.getGuestName(),
                parentTransaction.getEmail(),
                parentTransaction.getEnrolleCode(),
                parentTransaction.getCardNumber(),
                parentTransaction.getCreditCardType(),
                commission,
                transactionStatusDto,
                parentTransaction,
                transactionTypeDto,
                parentTransaction.getTransactionSubCategory(),
                netAmount,
                false,
                parentTransaction.getMerchantCurrency(),
                false
        ));
        command.setId(transactionDto.getId());
        if(parentAmountAndCommandAmount >= parentTransaction.getAmount()){
            parentTransaction.setPermitRefund(false);
            this.service.update(parentTransaction);
        }
        this.transactionStatusHistoryService.create(transactionDto, command.getEmployeeId());
    }
}
