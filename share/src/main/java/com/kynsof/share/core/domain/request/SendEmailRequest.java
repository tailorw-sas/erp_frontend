package com.kynsof.share.core.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class SendEmailRequest {

    private String toEmail;
    private String subject;
    private String message;
    private String name;
    private String file;

}
