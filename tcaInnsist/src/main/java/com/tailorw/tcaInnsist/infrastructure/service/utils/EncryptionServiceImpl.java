package com.tailorw.tcaInnsist.infrastructure.service.utils;

import com.tailorw.tcaInnsist.domain.services.utils.IEncryptionService;
import com.tailorw.tcaInnsist.infrastructure.repository.config.EncryptionProperties;
import com.tailorw.tcaInnsist.infrastructure.utils.exceptions.EncryptionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EncryptionServiceImpl implements IEncryptionService {

    private final EncryptionProperties properties;

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
        return Base64.getDecoder().decode(textInString);
    }

    private String bytesToString(byte[] textInBytes){
        return Base64.getEncoder().encodeToString(textInBytes);
    }
}
