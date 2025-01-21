package com.kynsoft.finamer.insis.infrastructure.services.utils;

import com.kynsoft.finamer.insis.domain.exceptions.EncryptionException;
import com.kynsoft.finamer.insis.domain.services.utils.IEncryptionService;
import com.kynsoft.finamer.insis.infrastructure.config.EncryptionProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements IEncryptionService {

    private final EncryptionProperties properties;

    public EncryptionServiceImpl(EncryptionProperties properties){
        this.properties = properties;
    }

    @Override
    public String encrypt(String plainText){
        byte[] keyBytes = stringToBytes(properties.getSecretKey());
        try{
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, properties.getAlgoritm());
            Cipher cipher = Cipher.getInstance(getCipherInstance());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToString(encryptedBytes);
        }catch (Exception ex){
            throw new EncryptionException("Error trying to encrypt: " + plainText, ex);
        }
    }

    @Override
    public String decrypt(String encryptedText){
        byte[] keyBytes = stringToBytes(properties.getSecretKey());
        byte[] decodedBytes = stringToBytes(encryptedText);

        try{
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, properties.getAlgoritm());
            Cipher cipher = Cipher.getInstance(getCipherInstance());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);


            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        }catch (Exception ex){
            throw new EncryptionException("Error trying to decrypt: " + encryptedText, ex);
        }

    }

    private String getCipherInstance(){
        return properties.getAlgoritm() + "/" + properties.getMode() + "/" + properties.getPadding();
    }

    private byte[] stringToBytes(String textInString){
        return Base64.getDecoder().decode(textInString.trim());
    }

    private String bytesToString(byte[] textInBytes){
        return Base64.getEncoder().encodeToString(textInBytes);
    }
}
