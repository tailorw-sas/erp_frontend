package com.kynsoft.notification.application.command.advertisingcontent.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConfigureTimeZone;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import com.kynsoft.notification.infrastructure.service.AmazonClient;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdvertisingContentCommandHandler implements ICommandHandler<UpdateAdvertisingContentCommand> {

    private final IAdvertisingContentService service;
    private final AmazonClient amazonClient;

    public UpdateAdvertisingContentCommandHandler(IAdvertisingContentService service, AmazonClient amazonClient) {
        this.service = service;
        this.amazonClient = amazonClient;
    }

    @Override
    public void handle(UpdateAdvertisingContentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "AdvertisingContent ID cannot be null."));
        AdvertisingContentDto update = this.service.findById(command.getId());

//        if (command.getImage() != null) {
//            try {
//                MultipartFile file = new CustomMultipartFile(command.getImage(), UUID.randomUUID().toString());
//                String url = amazonClient.save(file, file.getName());
//                update.setUrl(url);
//            } catch (IOException ex) {
//                Logger.getLogger(UpdateAdvertisingContentCommandHandler.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

        UpdateIfNotNull.updateIfNotNull(update::setTitle, command.getTitle());
        UpdateIfNotNull.updateIfNotNull(update::setDescription, command.getDescription());
        UpdateIfNotNull.updateIfNotNull(update::setLink, command.getLink());
        update.setType(command.getType());
        update.setUpdatedAt(ConfigureTimeZone.getTimeZone());
        update.setUrl(command.getImage());
        this.service.update(update);
    }
}
