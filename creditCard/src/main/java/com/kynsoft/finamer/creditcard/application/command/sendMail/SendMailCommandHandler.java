package com.kynsoft.finamer.creditcard.application.command.sendMail;

import com.kynsof.share.core.application.mailjet.*;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.UrlGetBase;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.services.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Component
@Transactional
public class SendMailCommandHandler implements ICommandHandler<SendMailCommand> {


    private final MailService mailService;
    private final ITransactionService transactionService;
    private final TokenService tokenService;
    private final IManageMerchantConfigService merchantConfigService;

    public SendMailCommandHandler(MailService mailService, ITransactionService transactionService, TokenService tokenService, IManageMerchantConfigService merchantConfigService) {

        this.mailService = mailService;
        this.transactionService = transactionService;
        this.tokenService = tokenService;
        this.merchantConfigService = merchantConfigService;
    }

    @Override
    @Transactional
    public void handle(SendMailCommand command) {
        sendEmail(command, command.getId());
    }
    private void sendEmail(SendMailCommand command, Long id) {

       TransactionDto transactionDto = transactionService.findById(id);
       String token = tokenService.generateToken(transactionDto.getTransactionUuid());
        if (transactionDto.getEmail() != null) {
            ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
            String baseUrl = UrlGetBase.getBaseUrl(merchantConfigDto.getSuccessUrl());
            String paymentLink = baseUrl + "payment?token="+ token;
            try {
                transactionService.sendTransactionPaymentLinkEmail(transactionDto, paymentLink);
                command.setResult(true);
            } catch (Exception e) {
                command.setResult(false);
            }
        }
    }


    }
