package com.kynsoft.notification.application.command.advertisingcontent.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConfigureTimeZone;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import com.kynsoft.notification.infrastructure.service.AmazonClient;
import org.springframework.stereotype.Component;

@Component
public class CreateAdvertisingContentCommandHandler implements ICommandHandler<CreateAdvertisingContentCommand> {

    private final IAdvertisingContentService service;
    private final AmazonClient amazonClient;

    public CreateAdvertisingContentCommandHandler(IAdvertisingContentService service, AmazonClient amazonClient) {
        this.service = service;
        this.amazonClient = amazonClient;
    }

    @Override
    public void handle(CreateAdvertisingContentCommand command) {
        String url = null;

        this.service.create(new AdvertisingContentDto(
                command.getId(), 
                command.getTitle(), 
                command.getDescription(), 
                command.getType(),
                ConfigureTimeZone.getTimeZone(),
                null,
                command.getImage(),
                command.getLink()
        ));
    }
}
