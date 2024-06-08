package com.kynsof.identity.domain.interfaces.service;

public interface IRedisService {
    void saveOtpCode(String email, String otpCode);
    String getOtpCode(String email);
    String generateOtpCode();
    void deleteKey(String key);
}
