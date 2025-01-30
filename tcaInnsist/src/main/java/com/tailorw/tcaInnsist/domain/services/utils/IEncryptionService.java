package com.tailorw.tcaInnsist.domain.services.utils;

public interface IEncryptionService {

    String encrypt(String plainText);
    String decrypt(String encryptedText);
}
