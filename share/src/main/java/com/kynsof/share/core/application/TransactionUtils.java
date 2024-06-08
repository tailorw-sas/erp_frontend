package com.kynsof.share.core.application;

import java.security.SecureRandom;
import java.util.Base64;

public class TransactionUtils {

    private static final SecureRandom random = new SecureRandom();

    public static String generateUniqueCode() {
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        String code = Base64.getUrlEncoder().encodeToString(bytes);
        return code.substring(0, 16);
    }

    public static String generateUniqueCodeNumber() {
        
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < 12; j++) {
            int randomDigit = random.nextInt(10);
            sb.append(randomDigit);
        }
        return sb.toString();
    }

    public static String generateUniqueCodeNumberInDuplicated() {

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < 9; j++) {
            int randomDigit = random.nextInt(10);
            sb.append(randomDigit);
        }
        return sb.toString();
    }

}
