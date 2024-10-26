package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationAdjustmentRequest;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.rules.adjustmentTransaction.AdjustmentTransactionAmountRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UpdateBankReconciliationCommandHandler implements ICommandHandler<UpdateBankReconciliationCommand> {

    private final IManageBankReconciliationService bankReconciliationService;

    private final ITransactionService transactionService;

    private final IManageAgencyService agencyService;

    private final IManageTransactionStatusService transactionStatusService;

    private final IManageVCCTransactionTypeService transactionTypeService;

    public UpdateBankReconciliationCommandHandler(IManageBankReconciliationService bankReconciliationService, ITransactionService transactionService, IManageAgencyService agencyService, IManageTransactionStatusService transactionStatusService, IManageVCCTransactionTypeService transactionTypeService) {
        this.bankReconciliationService = bankReconciliationService;
        this.transactionService = transactionService;
        this.agencyService = agencyService;
        this.transactionStatusService = transactionStatusService;
        this.transactionTypeService = transactionTypeService;
    }

    @Override
    public void handle(UpdateBankReconciliationCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Bank Reconciliation ID cannot be null."));
        ManageBankReconciliationDto dto = this.bankReconciliationService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateLocalDateTime(dto::setPaidDate, command.getPaidDate(), dto.getPaidDate(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRemark, command.getRemark(), dto.getRemark(), update::setUpdate);

        //comprobar si son diferentes los ids de las transactions para mandar a actualizar
        Set<Long> reconciliationTransactionIds = dto.getTransactions().stream().map(TransactionDto::getId).collect(Collectors.toSet());
        if (!reconciliationTransactionIds.equals(command.getTransactions())) {
            updateTransactions(dto, command.getTransactions());
        }

        if (command.getAdjustmentTransactions() != null && !command.getAdjustmentTransactions().isEmpty()) {
            List<Long> adjustmentIds = createAdjustments(command.getAdjustmentTransactions(), dto);
            command.setAdjustmentTransactionIds(adjustmentIds);
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
            this.transactionService.update(transactionDto);
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
                transactionList.add(transactionDto);
            }
        }
        reconciliation.setTransactions(transactionList);
        this.bankReconciliationService.update(reconciliation);
    }

    private List<Long> createAdjustments(List<UpdateBankReconciliationAdjustmentRequest> adjustmentRequest, ManageBankReconciliationDto reconciliationDto){
        List<Long> ids = new ArrayList<>();
        Set<TransactionDto> adjustmentTransactions = new HashSet<>();
        for (UpdateBankReconciliationAdjustmentRequest request : adjustmentRequest) {
            RulesChecker.checkRule(new AdjustmentTransactionAmountRule(request.getAmount()));

            ManageAgencyDto agencyDto = this.agencyService.findById(request.getAgency());
            ManageTransactionStatusDto transactionStatusDto = this.transactionStatusService.findByETransactionStatus(ETransactionStatus.RECEIVE);
            ManageVCCTransactionTypeDto transactionCategory = this.transactionTypeService.findById(request.getTransactionCategory());
            ManageVCCTransactionTypeDto transactionSubCategory = this.transactionTypeService.findById(request.getTransactionSubCategory());

            //todo: calculo de la commission
            double commission = 0;
            double netAmount = request.getAmount() - commission;

            TransactionDto transactionDto = this.transactionService.create(new TransactionDto(
                    UUID.randomUUID(),
                    agencyDto,
                    transactionCategory,
                    transactionSubCategory,
                    request.getAmount(),
                    request.getReservationNumber(),
                    request.getReferenceNumber(),
                    transactionStatusDto,
                    0.0,
                    LocalDate.now(),
                    netAmount,
                    LocalDate.now(),
                    false,
                    true
            ));
            adjustmentTransactions.add(transactionDto);
            ids.add(transactionDto.getId());
        }
        reconciliationDto.getTransactions().addAll(adjustmentTransactions);
        this.bankReconciliationService.update(reconciliationDto);
        return ids;
    }

}
