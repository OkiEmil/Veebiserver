package UserManagement;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

import static UserManagement.PasswordUtils.stringToHash;

public class Users {
    private static volatile Users instance; // singleton
    private final String usersFilePath="usersfile.ser"; // fail, kus kasutajad paroolidega hoitakse
    private final ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>(); // {username: [salt,hashedpassword]} kujul
    private final ObjectMapper mapper = new ObjectMapper();

    private Users() {
        mapUsersFromFile();
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

    /**
     * Laeb kasutajate HashMapi failist mällu serveri käivitamisel
     */
    private void mapUsersFromFile() {
        File file = new File(this.usersFilePath); // filePath is a String
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(this.usersFilePath, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    line=line.trim();
                    if (line.isEmpty()) continue;
                    User user = mapper.readValue(line, User.class);
                    this.userMap.put(user.getUsername(), user);
                } catch (IOException e) {
                    System.err.println("Skipped line: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read users file", e);
        }
    }

    /**
     * Kasulik kasutaja leidmiseks
     * @param username Kasutajanimi
     * @return kasutaja (get meetoditega), null - ei eksisteeri
     */
    public User findUser(String username) {
        if (userMap.containsKey(username))
            return this.userMap.get(username);
        else return null;
    }

    /**
     * Autenteerimisel tagastab, kas saab sisse logida (võrdleb parooli salvestatuga)
     * @param username Kasutajanimi
     * @param receivedPassword Sissetulev parool
     * @return kas saab sisse logida
     */
    public synchronized boolean isPasswordCorrect(String username,String receivedPassword) {
        String salt = this.userMap.get(username).getSalt();
        String savedPasswordHash = this.userMap.get(username).getHashedPassword();
        return PasswordUtils.isPasswordCorrect(receivedPassword,salt,savedPasswordHash);
    }

    /**
     * Lisab kasutaja süsteemi
     * @param username Kasutaja kasutajanimi
     * @param password Kasutaja parool
     * @return false - Kasutaja juba eksisteerib või kasutaja loomine ebaõnnestus,
     *         true - kasutaja lisati süsteemi
     */
    public synchronized boolean addUser(String username, String password)  {
        if ((findUser(username))!=null) {
            return false;
        }
        String salt = PasswordUtils.generateSalt();
        String hashedSaltedPassword = stringToHash(password+salt);
        User user = new User(); // teeb kasutaja
        user.setUserName(username);
        user.setSalt(salt);
        user.setHashedPassword(hashedSaltedPassword);

        this.userMap.put(username, user); // lisab kasutaja andmed faili, hashmappi
        appendUserToFile(user);
        return true;
    }


    /**
     * Lisab serveri jooksmise ajal kasutaja faili (et ei peaks tervet faili korraga ümber kirjutama iga väikese muudatusega)
     * @param user Faili lisatav kasutaja
     */
    private synchronized void appendUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath, StandardCharsets.UTF_8, true))) {
            writer.write(mapper.writeValueAsString(user));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to append user to file", e); // tuleb midagi korralikku ette võtta
        }
    }


    /**
     * Salvestab andmed faili, peaks enne serveri kinni panemist käivitama.
     * On atomic, st, et kõik tegevused tehakse korraga, ei korrupeeri faili
     */
    public synchronized void saveUsersToFIle() {
        Path temp = Paths.get(this.usersFilePath + ".tmp");
        Path original = Paths.get(this.usersFilePath);

        try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            for (User user : this.userMap.values()) {
                writer.write(this.mapper.writeValueAsString(user));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write user data from memory into a file", e);
        }
        try {
            Files.move(temp, original, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); // mv
        } catch (IOException e) {
            throw new RuntimeException("Failed to replace users file with data in memory (data in file " + this.usersFilePath + ".tmp)", e);
        }
    }
}
