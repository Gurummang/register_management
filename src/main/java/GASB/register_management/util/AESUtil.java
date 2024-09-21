package GASB.register_management.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@Slf4j
public class AESUtil {

    private static final String ALGORITHM = "AES";

    // Hex 문자열을 바이트 배열로 변환
    private static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    // 암호화
    public static String encrypt(String plainText, String key) {
        try {
            byte[] keyBytes = hexStringToByteArray(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for encryption: {}", e.getMessage());
            return null;
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            log.error("Encryption failed: bad padding or illegal block size: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error during encryption: {}", e.getMessage());
            return null;
        }
    }

    // 복호화
    public static String decrypt(String encryptedText, String key) {
        try {
            byte[] keyBytes = hexStringToByteArray(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument provided for decryption: {}", e.getMessage());
            return null;
        } catch (javax.crypto.BadPaddingException | javax.crypto.IllegalBlockSizeException e) {
            log.error("Decryption failed: bad padding or illegal block size: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error during decryption: {}", e.getMessage());
            return null;
        }
    }
}
