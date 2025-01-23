package com.kynsoft.finamer.insis.domain.services.utils;

public interface IEncryptionService {
    String encrypt(String plainText);
    String decrypt(String encryptedText);
}
