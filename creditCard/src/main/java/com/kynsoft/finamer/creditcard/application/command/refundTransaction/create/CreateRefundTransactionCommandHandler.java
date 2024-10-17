package com.kynsoft.finamer.creditcard.application.command.refundTransaction.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.CalculationType;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.refundTransaction.RefundTransactionCompareParentAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class CreateRefundTransactionCommandHandler implements ICommandHandler<CreateRefundTransactionCommand> {

    private final ITransactionService service;

    private final IParameterizationService parameterizationService;

    private final IManageTransactionStatusService transactionStatusService;

    private final IManageMerchantCommissionService manageMerchantCommissionService;

    public CreateRefundTransactionCommandHandler(ITransactionService service, IParameterizationService parameterizationService, IManageTransactionStatusService transactionStatusService, IManageMerchantCommissionService manageMerchantCommissionService) {
        this.service = service;
        this.parameterizationService = parameterizationService;
        this.transactionStatusService = transactionStatusService;
        this.manageMerchantCommissionService = manageMerchantCommissionService;
    }

    @Override
    public void handle(CreateRefundTransactionCommand command) {
        RulesChecker.checkRule(new RefundTransactionCompareParentAmountRule(this.service, command.getParentId(), command.getAmount()));
        TransactionDto parentTransaction = this.service.findById(command.getParentId());
        if(!parentTransaction.getPermitRefund()){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_CANNOT_BE_REFUNDED, new ErrorField("refund", DomainErrorMessage.VCC_TRANSACTION_CANNOT_BE_REFUNDED.getReasonPhrase())));
        }

        double commission = 0.0;
        double netAmount = command.getAmount();
        if(command.getHasCommission()
                && parentTransaction.getMerchant() != null
                && parentTransaction.getCreditCardType() != null){

            UUID parentMerchantId = parentTransaction.getMerchant().getId();
            UUID parentCreditCardTypeId = parentTransaction.getCreditCardType().getId();
            LocalDate parentCheckIn = parentTransaction.getCheckIn();
            ManageMerchantCommissionDto merchantCommissionDto = this.manageMerchantCommissionService
                    .findByManagerMerchantAndManageCreditCartTypeAndDateWithinRangeOrNoEndDate(
                            parentMerchantId, parentCreditCardTypeId, parentCheckIn
                    );
            double merchantCommission = merchantCommissionDto.getCommission();
            if(merchantCommissionDto.getCalculationType().compareTo(CalculationType.PER) == 0){
                commission = BigDecimal.valueOf(merchantCommission / 100 * command.getAmount()).setScale(2, RoundingMode.HALF_UP).doubleValue();

                // Realizar la división usando BigDecimal para mantener la precisión
                BigDecimal result = BigDecimal.valueOf(command.getAmount() - commission);

                // Redondear el resultado a dos decimales hacia el "entero más cercano" donde
                // todos los números que son exactamente a medio camino se
                // redondean hacia arriba. Convirtiendo el resultado a double
                netAmount = result.setScale(2, RoundingMode.HALF_UP).doubleValue();
            } else if(merchantCommissionDto.getCalculationType().compareTo(CalculationType.FIX) == 0){
                netAmount = command.getAmount() - commission;
            }
        }

        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByETransactionStatus(ETransactionStatus.REFUND);

        Long id = this.service.create(new TransactionDto(
                parentTransaction.getTransactionUuid(),
                parentTransaction.getMerchant(),
                parentTransaction.getMethodType(),
                parentTransaction.getHotel(),
                parentTransaction.getAgency(),
                parentTransaction.getLanguage(),
                command.getAmount(),
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
                parentTransaction.getTransactionCategory(),
                parentTransaction.getTransactionSubCategory(),
                netAmount,
                false,
                parentTransaction.getMerchantCurrency(),
                false
        ));
        command.setId(id);
        if(this.service.findSumOfAmountByParentId(parentTransaction.getId()) >= parentTransaction.getAmount()){
            parentTransaction.setPermitRefund(false);
            this.service.update(parentTransaction);
        }
    }
}
