package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.manageBankReconciliation.TransactionCheckInCloseOperationRule;
import com.kynsoft.finamer.creditcard.domain.rules.transaction.TransactionInReconciliationRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UpdateBankReconciliationCommandHandler implements ICommandHandler<UpdateBankReconciliationCommand> {

    private final IManageBankReconciliationService bankReconciliationService;

    private final ITransactionService transactionService;

    private final IBankReconciliationAdjustmentService bankReconciliationAdjustmentService;

    private final IManageReconcileTransactionStatusService transactionStatusService;

    private final IBankReconciliationStatusHistoryService bankReconciliationStatusHistoryService;

    private final ICreditCardCloseOperationService closeOperationService;

    private final IParameterizationService parameterizationService;

    public UpdateBankReconciliationCommandHandler(IManageBankReconciliationService bankReconciliationService, ITransactionService transactionService, IBankReconciliationAdjustmentService bankReconciliationAdjustmentService, IManageReconcileTransactionStatusService transactionStatusService, IBankReconciliationStatusHistoryService bankReconciliationStatusHistoryService, ICreditCardCloseOperationService closeOperationService, IParameterizationService parameterizationService) {
        this.bankReconciliationService = bankReconciliationService;
        this.transactionService = transactionService;
        this.bankReconciliationAdjustmentService = bankReconciliationAdjustmentService;
        this.transactionStatusService = transactionStatusService;
        this.bankReconciliationStatusHistoryService = bankReconciliationStatusHistoryService;
        this.closeOperationService = closeOperationService;
        this.parameterizationService = parameterizationService;
    }

    @Override
    public void handle(UpdateBankReconciliationCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Bank Reconciliation ID cannot be null."));
        ManageBankReconciliationDto dto = this.bankReconciliationService.findById(command.getId());
        if (command.getPaidDate() != null) {
            RulesChecker.checkRule(new TransactionCheckInCloseOperationRule(this.closeOperationService, command.getPaidDate(), dto.getHotel().getId()));
        }
        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateLocalDateTime(dto::setPaidDate, command.getPaidDate(), dto.getPaidDate(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRemark, command.getRemark(), dto.getRemark(), update::setUpdate);
        UpdateIfNotNull.updateDouble(dto::setAmount, command.getAmount(), dto.getAmount(), update::setUpdate);

        //comprobar si no es nula la lista y son diferentes los ids de las transactions para mandar a actualizar
        Set<Long> reconciliationTransactionIds = dto.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet());
        if (command.getTransactions() != null && !reconciliationTransactionIds.equals(command.getTransactions())) {
            updateTransactions(dto, command.getTransactions());
        }

        if (command.getAdjustmentTransactions() != null && !command.getAdjustmentTransactions().isEmpty()) {
            List<Long> adjustmentIds = this.bankReconciliationAdjustmentService.createAdjustments(command.getAdjustmentTransactions(), dto);
            command.setAdjustmentTransactionIds(adjustmentIds);
        }

        if (command.getReconcileStatus() != null && !command.getReconcileStatus().equals(dto.getReconcileStatus().getId())){
            ManageReconcileTransactionStatusDto transactionStatusDto = this.transactionStatusService.findById(command.getReconcileStatus());
            updateStatus(dto, transactionStatusDto, command.getEmployeeId());
        }

        if(update.getUpdate() > 0) {
            this.bankReconciliationService.update(dto);
        }
    }

    private void updateTransactions(ManageBankReconciliationDto reconciliation, Set<Long> newTransactions) {
        // Identifica las transacciones que ya no deben estar asociadas a reconciliation
        Set<Long> transactionsToUnlink = reconciliation.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet());
        transactionsToUnlink.removeAll(newTransactions);

        // Desasocia las transacciones que ya no deberían estar vinculadas
        for (Long id : transactionsToUnlink) {
            TransactionDto transactionDto = this.transactionService.findById(id);
            transactionDto.setReconciliation(null);
            reconciliation.setDetailsAmount(reconciliation.getDetailsAmount() - transactionDto.getNetAmount());
            //si es de ajuste se elimina
            if(transactionDto.isAdjustment()){
                this.transactionService.delete(transactionDto);
            }
        }

        // Actualiza la colección en ManageBankReconciliation
        Set<TransactionDto> transactionList = new HashSet<>();
        Set<Long> reconcileTransactions = reconciliation.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet());
        for (Long transactionId : newTransactions) {
            if(!reconcileTransactions.contains(transactionId)) {
                TransactionDto transactionDto = this.transactionService.findById(transactionId);
                RulesChecker.checkRule(new TransactionInReconciliationRule(transactionDto));
                reconciliation.setDetailsAmount(reconciliation.getDetailsAmount() + transactionDto.getNetAmount());
                transactionList.add(transactionDto);
            }
        }
        reconciliation.setTransactions(transactionList);
        this.bankReconciliationService.update(reconciliation);
    }

    private void updateStatus(ManageBankReconciliationDto dto, ManageReconcileTransactionStatusDto transactionStatusDto, UUID employeeId){
        if (transactionStatusDto.isCompleted()){
            ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();
            //si no encuentra la parametrization que agarre 2 decimales por defecto
            int decimals = parameterizationDto != null ? parameterizationDto.getDecimals() : 2;
            double amount = BankerRounding.round(dto.getAmount(), decimals);
            double details = BankerRounding.round(dto.getDetailsAmount(), decimals);
            if (amount == details) {
                Set<TransactionDto> updatedTransactions = this.transactionService.changeAllTransactionStatus(dto.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet()), ETransactionStatus.RECONCILED, employeeId);
                dto.setReconcileStatus(transactionStatusDto);
                dto.setTransactions(updatedTransactions);
                this.bankReconciliationService.update(dto);
                this.bankReconciliationStatusHistoryService.create(dto, employeeId);
            } else {
                throw new BusinessException(
                        DomainErrorMessage.MANAGE_BANK_RECONCILIATION_COMPLETED_STATUS,
                        DomainErrorMessage.MANAGE_BANK_RECONCILIATION_COMPLETED_STATUS.getReasonPhrase()
                );
            }
        } else if (transactionStatusDto.isCancelled()){
            if (dto.getTransactions().isEmpty() && !dto.getReconcileStatus().isCompleted()) {
                dto.setReconcileStatus(transactionStatusDto);
                this.bankReconciliationService.update(dto);
                this.bankReconciliationStatusHistoryService.create(dto, employeeId);
            } else {
                throw new BusinessException(
                        DomainErrorMessage.MANAGE_BANK_RECONCILIATION_CANCELLED_STATUS,
                        DomainErrorMessage.MANAGE_BANK_RECONCILIATION_CANCELLED_STATUS.getReasonPhrase()
                );
            }
        } else if (transactionStatusDto.isCreated()){
            dto.setReconcileStatus(transactionStatusDto);
            this.bankReconciliationService.update(dto);
            this.bankReconciliationStatusHistoryService.create(dto, employeeId);
        }
    }

}
