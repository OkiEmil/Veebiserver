package UserManagement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class Users {
    private static volatile Users instance; // singleton
    private final String usersFilePath=""; // fail, kus kasutajad paroolidega hoitakse
    private ConcurrentHashMap<String, String[]> userMap; // {username: [salt,hashedpassword]} kujul

    private Users() {
        this.userMap = mapUsersFromFile(usersFilePath);
    }
    public static Users getInstance() {
        if (instance == null) {
            synchronized (Users.class) {
                if (instance == null) {
                    instance = new Users();
                }
            }
        }
        return instance;
    }

    private ConcurrentHashMap<String, String[]> mapUsersFromFile(String usersFilePath) {
        ConcurrentHashMap<String, String[]> users = new ConcurrentHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath, StandardCharsets.UTF_8))) {
            String rida;
            while ((rida= reader.readLine())!=null) {
                String[] reaTykid=rida.split("; ");
                users.put(reaTykid[0],new String[]{reaTykid[1],reaTykid[2]});
            }
            return users;
        } catch (Exception e) {
            throw new RuntimeException(e); // tuleb midagi normaalset ette võtta !!!
        }
    }
    public void saveUserToFile(String userName, String[] password) throws IOException { // exceptioniga midagi ette võtta !!!
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.usersFilePath,true))) {
            writer.write(userName + "; " + password[0] + "; " + password[1] + "\n");
        }
    }

    public boolean doesUserExist(String userName) {
        return userMap.containsKey(userName);
    }

    public synchronized void addUser(String userName, String password)  {
        if (doesUserExist(userName)) {
            throw new IllegalArgumentException("User already exists."); // vist ei ole norm, peab muutma !!!
        }
        try {
            String salt = PasswordUtils.generateSalt();
            String hashedSaltedPassword = PasswordUtils.stringToHash(password+salt);

            saveUserToFile(userName,new String[]{salt,hashedSaltedPassword}); // lisab kasutaja andmed faili, hashmappi
            this.userMap.put(userName, new String[]{salt, hashedSaltedPassword});
        } catch (IOException e) { // ei õnnestunud kontot salvestada ehk luua
            throw new RuntimeException(e); // jällegi midagi ette võtta !!!
        }
    }


}
