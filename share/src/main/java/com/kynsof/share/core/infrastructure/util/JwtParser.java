package com.kynsof.share.core.infrastructure.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Objects;

public final class JwtParser {

    private JwtParser() {
        throw new IllegalStateException("Utility class to parse JWT tokens");
    }

    public static Claims parseToken(String token) {
        Objects.requireNonNull(token, "Token can't be null");
        token = token.replace("Bearer" + " ", "");
        String[] splitToken = token.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        return Jwts.parserBuilder().build().parseClaimsJwt(unsignedToken).getBody();
    }
}
