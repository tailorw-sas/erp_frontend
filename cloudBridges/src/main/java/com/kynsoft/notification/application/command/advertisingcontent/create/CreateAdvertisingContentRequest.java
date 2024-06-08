package com.kynsoft.notification.application.command.advertisingcontent.create;

import com.kynsoft.notification.domain.dto.ContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdvertisingContentRequest {
    private String title;
    private String description;
    private ContentType type;
    private String image;
    private String link;
}
