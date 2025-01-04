package com.kynsoft.finamer.payment.application.command.paymentImport.detail.attachment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment.CreateAttachmentEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PaymentImportDetailAttachmentCommandHandler implements ICommandHandler<PaymentImportDetailAttachmentCommand> {
    private final ApplicationEventPublisher applicationEventPublisher;

    public PaymentImportDetailAttachmentCommandHandler(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void handle(PaymentImportDetailAttachmentCommand command) {

        if (Objects.nonNull(command.getPaymentIds())) {
            CreateAttachmentEvent createAttachmentEvent =
                    new CreateAttachmentEvent(this, command.getPaymentIds(), command.getFile(),
                          command.getEmployee()
                            , command.getFileName(),command.getFileLength());
            applicationEventPublisher.publishEvent(createAttachmentEvent);

        }
    }
}
