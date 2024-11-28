package com.kynsoft.finamer.creditcard.application.command.attachment.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentService;
import com.kynsoft.finamer.creditcard.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAttachmentCommandHandler implements ICommandHandler<DeleteAttachmentCommand> {

    private final IAttachmentService service;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final ITransactionService transactionService;

    public DeleteAttachmentCommandHandler(IAttachmentService service, IAttachmentStatusHistoryService attachmentStatusHistoryService, ITransactionService transactionService) {
        this.service = service;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.transactionService = transactionService;
    }

    @Override
    public void handle(DeleteAttachmentCommand command) {
        AttachmentDto delete = this.service.findById(command.getId());
        TransactionDto transactionDto = delete.getTransaction() != null ? this.transactionService.findById(delete.getTransaction().getId()) : null;

        if (transactionDto != null && delete.getType().isDefaults()) {
            int cont = 0;
            for (AttachmentDto attachmentDto : transactionDto.getAttachments()) {
                cont += attachmentDto.getType().isDefaults() ? 1 : 0;
            }
            if (cont < 2) {
                throw new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.TRANSACTION_MUST_HAVE_ATTACHMENT_TYPE,
                        new ErrorField("type", DomainErrorMessage.TRANSACTION_MUST_HAVE_ATTACHMENT_TYPE.getReasonPhrase())));
            }
        }

        this.service.delete(delete);
        this.attachmentStatusHistoryService.create(delete, "deleted");
    }
}
