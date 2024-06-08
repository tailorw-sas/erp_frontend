package com.kynsoft.notification.application.command.advertisingcontent.update;

import com.kynsoft.notification.domain.dto.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateAdvertisingContentRequest {
    private String title;
    private String description;
    private ContentType type;
    private String image;
    private String link;

}
