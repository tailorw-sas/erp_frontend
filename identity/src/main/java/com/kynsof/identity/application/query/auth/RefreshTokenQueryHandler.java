package com.kynsof.identity.application.query.auth;

import com.kynsof.identity.application.command.auth.autenticate.TokenResponse;
import com.kynsof.identity.infrastructure.services.AuthService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenQueryHandler implements IQueryHandler<RefreshTokenQuery, TokenResponse> {

    private final AuthService serviceImpl;

    public RefreshTokenQueryHandler(AuthService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public TokenResponse handle(RefreshTokenQuery query) {

        return serviceImpl.refreshToken(query.getRefreshToken());
    }
}
