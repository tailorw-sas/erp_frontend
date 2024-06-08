package com.kynsof.share.core.application.payment.domain.placeToPlay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

        private int code;

        private String message;

        private String details;
}
