package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TokenResponse implements IResponse {

    private String token;
    private long expiration;

    public TokenResponse(String token, long expiration) {
        this.token = token;
        this.expiration = expiration;

    }
}
