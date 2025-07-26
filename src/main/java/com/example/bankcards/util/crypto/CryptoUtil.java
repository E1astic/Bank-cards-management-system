package com.example.bankcards.util.crypto;

import com.example.bankcards.exception.crypto.DecryptionException;
import com.example.bankcards.exception.crypto.EncryptionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class CryptoUtil {

    private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";

    @Value("${encrypt-key}")
    private String SECRET_KEY;

    public String encrypt(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException | InvalidKeyException e) {
            throw new EncryptionException("Ошибка шифрования номера карты.\n" + e.getMessage());
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted);
        } catch(NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeyException e) {
            throw new DecryptionException("Ошибка дешифрования номера карты.\n" + e.getMessage());
        }
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        int length = cardNumber.length();
        String lastFour = cardNumber.substring(length - 4);
        return "**** **** **** " + lastFour;
    }
}
