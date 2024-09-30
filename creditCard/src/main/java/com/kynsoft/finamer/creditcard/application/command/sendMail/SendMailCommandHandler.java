package com.kynsoft.finamer.creditcard.application.command.sendMail;

import com.kynsof.share.core.application.mailjet.*;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
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

    public SendMailCommandHandler(MailService mailService, ITransactionService transactionService, TokenService tokenService) {

        this.mailService = mailService;
        this.transactionService = transactionService;
        this.tokenService = tokenService;

    }

    @Override
    @Transactional
    public void handle(SendMailCommand command) {
        sendEmail(command, command.getToken());
    }

    private void sendEmail(SendMailCommand command, String token) {

       UUID transactionUuid = UUID.fromString(tokenService.validateToken(token).getId());
       TransactionDto transactionDto = transactionService.findByUuid(transactionUuid);
        if (transactionDto.getEmail() != null) {
            SendMailJetEMailRequest request = new SendMailJetEMailRequest();
            request.setTemplateId(6324713); // Cambiar en configuración

            // Variables para el template de email
            List<MailJetVar> vars = Arrays.asList(
                    new MailJetVar("payment_link", "https://kynsoft.com/" + "?param=" + "Payment" + "&var=" + token),
                    new MailJetVar("invoice_amount", transactionDto.getAmount().toString())
            );
            request.setMailJetVars(vars);

            // Recipients
            List<MailJetRecipient> recipients = new ArrayList<>();
            recipients.add(new MailJetRecipient(transactionDto.getEmail(), transactionDto.getGuestName()));
            request.setRecipientEmail(recipients);

            try {
                mailService.sendMail(request);
                command.setResult(true);
            } catch (Exception e) {
                command.setResult(false);
            }
        }
    }


    }
