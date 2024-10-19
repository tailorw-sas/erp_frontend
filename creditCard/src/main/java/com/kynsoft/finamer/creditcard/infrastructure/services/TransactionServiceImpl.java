package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.application.mailjet.MailJetRecipient;
import com.kynsof.share.core.application.mailjet.MailJetVar;
import com.kynsof.share.core.application.mailjet.MailService;
import com.kynsof.share.core.application.mailjet.SendMailJetEMailRequest;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionResponse;
import com.kynsoft.finamer.creditcard.application.query.transaction.search.TransactionSearchResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.Transaction;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.TransactionWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TransactionReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TransactionServiceImpl implements ITransactionService {
    
    @Autowired
    private final TransactionWriteDataJPARepository repositoryCommand;
    
    @Autowired
    private final TransactionReadDataJPARepository repositoryQuery;

    @Autowired
    private final ManageTransactionStatusServiceImpl statusService;

    @Autowired
    private final MailService mailService;

    public TransactionServiceImpl(TransactionWriteDataJPARepository repositoryCommand, TransactionReadDataJPARepository repositoryQuery, ManageTransactionStatusServiceImpl statusService, MailService mailService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.statusService = statusService;
        this.mailService = mailService;
    }

    @Override
    public Long create(TransactionDto dto) {
        Transaction entity = new Transaction(dto);
        return this.repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(TransactionDto dto) {
        Transaction entity = new Transaction(dto);
        entity.setUpdateAt(LocalDateTime.now());
        
        this.repositoryCommand.save(entity);
    }

    @Override
    public void delete(TransactionDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public TransactionDto findById(Long id) {
        Optional<Transaction> entity = this.repositoryQuery.findById(id);
        if (entity.isPresent()) {
            return entity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND.getReasonPhrase())));
    }
    @Override
    public TransactionDto findByUuid(UUID uuid) {
        Optional<Transaction> entity = this.repositoryQuery.findByTransactionUuid(uuid);
        if (entity.isPresent()) {
            return entity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Transaction> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Transaction> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedSearchResponse(data);
    }

    @Override
    public Long countByReservationNumberAndManageHotelIdAndNotId(String reservationNumber, UUID hotel) {
        return this.repositoryQuery.countByReservationNumberAndManageHotelIdAndNotId(reservationNumber, hotel);
    }

    @Override
    public boolean compareParentTransactionAmount(Long parentId, Double amount) {
        Optional<Transaction> parentTransaction = this.repositoryQuery.findById(parentId);
        if (parentTransaction.isPresent()) {
            double parentAmount = parentTransaction.get().getAmount();
            Optional<Double> sumOfChildrenAmount = this.repositoryQuery.findSumOfAmountByParentId(parentId);
            return (sumOfChildrenAmount.orElse(0.0) + amount) > parentAmount;
        } else {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND.getReasonPhrase())));
        }
    }

    @Override
    public Double findSumOfAmountByParentId(Long parentId) {
        return this.repositoryQuery.findSumOfAmountByParentId(parentId).orElse(0.0);
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("methodType".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    MethodType enumValue = MethodType.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum MethodType: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<Transaction> data) {
        List<TransactionResponse> responseList = new ArrayList<>();
        for (Transaction entity : data.getContent()) {
            responseList.add(new TransactionResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private PaginatedResponse getPaginatedSearchResponse(Page<Transaction> data) {
        List<TransactionSearchResponse> responseList = new ArrayList<>();
        for (Transaction entity : data.getContent()) {
            responseList.add(new TransactionSearchResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    //Conformar el correo para confirmar que la transaccion fue Recivida
    public void confirmTransactionMail(TransactionDto transactionDto){
        if(transactionDto.getEmail() != null){
            SendMailJetEMailRequest request = new SendMailJetEMailRequest();
            request.setTemplateId(6395138); // Cambiar en configuración

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String transactionDateStr = transactionDto.getPaymentDate() != null ? transactionDto.getPaymentDate().format(formatter) : "";

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
            String formattedAmount = currencyFormatter.format(transactionDto.getAmount());

            // Variables para el template de email, cambiar cuando keimer genere la plantilla
            List<MailJetVar> vars = Arrays.asList(
                    new MailJetVar("commerce", "Finamer Do"),
                    new MailJetVar("merchant", transactionDto.getMerchant().getDescription()),
                    new MailJetVar("hotel", transactionDto.getHotel().getName()),
                    new MailJetVar("number_id", transactionDto.getId()),
                    new MailJetVar("transaction_type", "Sale"),
                    new MailJetVar("status", transactionDto.getStatus().getName()),
                    new MailJetVar("payment_date", transactionDateStr),
                    new MailJetVar("authorization_number", "N/A"),
                    new MailJetVar("card_type", transactionDto.getCreditCardType().getName()),
                    new MailJetVar("card_number", transactionDto.getCardNumber()),
                    new MailJetVar("amount_usd", formattedAmount),
                    new MailJetVar("ibtis_usd", "$0.00"),
                    new MailJetVar("reference", transactionDto.getReferenceNumber()),
                    new MailJetVar("user", transactionDto.getGuestName()),
                    new MailJetVar("modality", transactionDto.getMethodType().name())
            );
            request.setMailJetVars(vars);

            // Recipients
            List<MailJetRecipient> recipients = new ArrayList<>();
            if (transactionDto.getEmail() != null && !transactionDto.getEmail().isEmpty()) {
                recipients.add(new MailJetRecipient(transactionDto.getEmail(), transactionDto.getGuestName()));
            }
            if (transactionDto.getHotelContactEmail() != null && !transactionDto.getHotelContactEmail().isEmpty()) {
                recipients.add(new MailJetRecipient(transactionDto.getHotelContactEmail(), transactionDto.getGuestName()));
            }
            request.setRecipientEmail(recipients);

            mailService.sendMail(request);
        }
    }

}
