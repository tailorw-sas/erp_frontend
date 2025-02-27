package com.kynsoft.notification.application.command.advertisingcontent.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConfigureTimeZone;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import org.springframework.stereotype.Component;

@Component
public class UpdateAdvertisingContentCommandHandler implements ICommandHandler<UpdateAdvertisingContentCommand> {

    private final IAdvertisingContentService service;

    public UpdateAdvertisingContentCommandHandler(IAdvertisingContentService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateAdvertisingContentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "AdvertisingContent ID cannot be null."));
        AdvertisingContentDto update = this.service.findById(command.getId());

        UpdateIfNotNull.updateIfNotNull(update::setTitle, command.getTitle());
        UpdateIfNotNull.updateIfNotNull(update::setDescription, command.getDescription());
        UpdateIfNotNull.updateIfNotNull(update::setLink, command.getLink());
        update.setType(command.getType());
        update.setUpdatedAt(ConfigureTimeZone.getTimeZone());
        update.setUrl(command.getImage());
        this.service.update(update);
    }
}
