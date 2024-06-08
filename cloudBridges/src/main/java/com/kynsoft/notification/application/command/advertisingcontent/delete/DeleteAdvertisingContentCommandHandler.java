package com.kynsoft.notification.application.command.advertisingcontent.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import org.springframework.stereotype.Component;

@Component
public class DeleteAdvertisingContentCommandHandler implements ICommandHandler<DeleteAdvertisingContentCommand> {

    private final IAdvertisingContentService serviceImpl;

    public DeleteAdvertisingContentCommandHandler(IAdvertisingContentService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void handle(DeleteAdvertisingContentCommand command) {

        AdvertisingContentDto delete = this.serviceImpl.findById(command.getId());
        serviceImpl.delete(delete);
    }

}
