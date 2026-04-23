package UserManagement;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtils {
    public static String generateSalt() {
        byte[] salt = new byte[32];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    public synchronized static String stringToHash(String password,String salt) {
        try {
            int iterationCount=310000; // ~+500ms latency register and login, to make bruteforcing difficult
            int outputLengthBits=256;
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterationCount, outputLengthBits);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // TODO: implement error handling
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isPasswordCorrect(String receivedPassword, String salt, String savedPasswordHash) {
        return stringToHash(receivedPassword,salt).equals(savedPasswordHash);
    }
}
