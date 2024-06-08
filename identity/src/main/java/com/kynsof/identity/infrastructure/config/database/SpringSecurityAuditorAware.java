package com.kynsof.identity.infrastructure.config.database;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("username");// O devuelve Optional.of("usuarioPorDefecto") si lo prefieres.
    }
}
