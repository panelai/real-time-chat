package swd392.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final String secret = "k99U4skldcQjt2yHKq/J9jYHhwtCpokJeuBIvJoeyqk=";

    private static final String ENCRYPTING_ALGORITHM = "AES";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static SecretKey getKeyFromString(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ENCRYPTING_ALGORITHM);
    }

    public static String encrypt(String dataString) throws Exception {
        SecretKey key = getKeyFromString(secret);
        Cipher cipher = Cipher.getInstance(ENCRYPTING_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //String dataString = objectMapper.writeValueAsString(data);
        byte[] encryptedData = cipher.doFinal(dataString.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKey key = getKeyFromString(secret);
        Cipher cipher = Cipher.getInstance(ENCRYPTING_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }

    public static String decodeUri(String data) {
        if (data == null) {
            return null;
        }
        return data.replace("_SLASH_", "/")
                .replace("_EQ_", "=")
                .replace("_QMARK_", "?")
                .replace("_AND_", "&");
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
}
