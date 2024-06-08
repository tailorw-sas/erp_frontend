package com.kynsof.identity.application.command.walletTransaction.create;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.identity.domain.dto.WalletDto;
import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.identity.domain.dto.enumType.TransactionType;
import com.kynsof.identity.domain.interfaces.service.ICustomerService;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.identity.domain.interfaces.service.IWalletService;
import com.kynsof.identity.domain.interfaces.service.IWalletTransactionService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.producer.s3.ProducerSaveFileEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CreateWalletTransactionCommandHandler implements ICommandHandler<CreateWalletTransactionCommand> {

    private final ICustomerService customerService;
    private final IWalletTransactionService walletTransactionService;
    private final IWalletService walletService;

    @Autowired
    private ProducerSaveFileEventService saveFileEventService;

    @Autowired
    private IGeographicLocationService geographicLocationService;

    public CreateWalletTransactionCommandHandler(ICustomerService service, IWalletTransactionService walletTransactionService, IWalletService walletService) {
        this.customerService = service;
        this.walletTransactionService = walletTransactionService;
        this.walletService = walletService;
    }

    @Override
    public void handle(CreateWalletTransactionCommand command) {
        CustomerDto customerDto = customerService.findById(command.getCustomerId());
        WalletDto walletDto = walletService.findByCustomerId(customerDto.getId());
        walletDto.setBalance(walletDto.getBalance().add(command.getAmount()));
        WalletTransactionDto walletTransactionDto = new WalletTransactionDto();
        walletTransactionDto.setId(UUID.randomUUID());
        walletTransactionDto.setWalletDto(walletDto);
        walletTransactionDto.setWalletId(walletDto.getId());
        walletTransactionDto.setAmount(command.getAmount());
        walletTransactionDto.setType(TransactionType.DEPOSIT);
        walletTransactionDto.setTransactionDate(LocalDateTime.now());
        walletTransactionDto.setDescription(command.getDescription());
        walletTransactionDto.setAuthorizationCode(command.getAuthorizationCode());
        walletTransactionDto.setRequestId(command.getRequestId());
        UUID id = walletTransactionService.create(walletTransactionDto);
        command.setId(id);
    }
}