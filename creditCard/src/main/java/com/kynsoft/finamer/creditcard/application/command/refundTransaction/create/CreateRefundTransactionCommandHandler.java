package com.kynsoft.finamer.creditcard.application.command.refundTransaction.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.rules.refundTransaction.RefundTransactionCompareParentAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageTransactionStatusService;
import com.kynsoft.finamer.creditcard.domain.services.IParameterizationService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CreateRefundTransactionCommandHandler implements ICommandHandler<CreateRefundTransactionCommand> {

    private final ITransactionService service;

    private final IParameterizationService parameterizationService;

    private final IManageTransactionStatusService transactionStatusService;

    public CreateRefundTransactionCommandHandler(ITransactionService service, IParameterizationService parameterizationService, IManageTransactionStatusService transactionStatusService) {
        this.service = service;
        this.parameterizationService = parameterizationService;
        this.transactionStatusService = transactionStatusService;
    }

    @Override
    public void handle(CreateRefundTransactionCommand command) {
        RulesChecker.checkRule(new RefundTransactionCompareParentAmountRule(this.service, command.getParentId(), command.getAmount()));
        TransactionDto parentTransaction = this.service.findById(command.getParentId());
        if(parentTransaction.getIsAdjustment()){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_REFUND_CANNOT_BE_ADJUSTMENT, new ErrorField("id", DomainErrorMessage.VCC_REFUND_CANNOT_BE_ADJUSTMENT.getReasonPhrase())));
        }

        double commission = 0.0;
        if(command.getHasCommission()){
            commission = 0.0;
        }
        double netAmount = command.getAmount() - commission;

        ParameterizationDto parameterizationDto = parameterizationService.findActiveParameterization();
        if(Objects.isNull(parameterizationDto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "No active parameterization")));
        }
        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByCode(parameterizationDto.getRefundTransactionStatusCode());

        Long id = this.service.create(new TransactionDto(
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
                netAmount
        ));
        command.setId(id);
    }
}
