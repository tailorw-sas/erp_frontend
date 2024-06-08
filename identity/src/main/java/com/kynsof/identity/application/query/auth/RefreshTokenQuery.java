package com.kynsof.identity.application.query.auth;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class RefreshTokenQuery implements IQuery {

    private final String refreshToken;

    public RefreshTokenQuery(String id) {
        this.refreshToken = id;
    }

}
