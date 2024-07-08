package com.kynsoft.notification.application.command.file.saveFileS3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SaveFileS3Request {

    private MultipartFile multipartFile;
    private String fonder;

}
