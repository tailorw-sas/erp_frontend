package com.kynsof.identity.domain.interfaces.service;

import com.kynsof.identity.application.query.users.userMe.UserMeResponse;

import java.util.UUID;

public interface IUserMeService {
    UserMeResponse getUserInfo(UUID userId);
}
