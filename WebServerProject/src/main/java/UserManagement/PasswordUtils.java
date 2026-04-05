package UserManagement;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    public synchronized static String stringToHash(String saltedPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
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
            throw new RuntimeException(e); // tuleb midagi normaalset ette võtta !!!
        }
    }
    public static boolean isPasswordCorrect(String receivedPassword, String salt, String savedPasswordHash) {
        return stringToHash(receivedPassword+salt).equals(savedPasswordHash);
    }

//    public static void main(String[] args) { // töötamise testimiseks, kustuta hiljem
//        String salt = generateSalt();
//        System.out.println(salt);
//        System.out.println(stringToHash("test123" + salt));
//        System.out.println(stringToHash("test123"));
//        if (isPasswordCorrect("test123",salt,stringToHash("test123" + salt))) {
//            System.out.println("töötab");
//        }
//    }
}
