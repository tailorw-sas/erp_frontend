package com.kynsoft.notification.application.command.advertisingcontent.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.notification.domain.dto.ContentType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAdvertisingContentCommand implements ICommand {

    private UUID id;
    private String title;
    private String description;
    private ContentType type;
    private String image;
    private String link;

    public UpdateAdvertisingContentCommand(UUID id, String title, String description, ContentType type, String image, String link) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.image = image;
        this.link = link;
    }

    public static UpdateAdvertisingContentCommand fromRequest(UpdateAdvertisingContentRequest request, UUID id) {
        return new UpdateAdvertisingContentCommand(
                id, 
                request.getTitle(), 
                request.getDescription(), 
                request.getType(), 
                request.getImage(), 
                request.getLink()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAdvertisingContentMessage(id);
    }
}
