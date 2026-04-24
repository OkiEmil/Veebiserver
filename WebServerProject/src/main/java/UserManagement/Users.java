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
    private final String usersFilePath="usersfile.ser"; // file, where users are stored
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
     * Loads the user data into memory upon starting the server
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
     * Returns the user from the hashmap
     * @param username Username
     * @return User, null - Does not exist
     */
    public User findUser(String username) {
        if (userMap.containsKey(username))
            return this.userMap.get(username);
        else return null;
    }

    /**
     * Authenticates the user
     * @param username User's username
     * @param receivedPassword Incoming password
     * @return Can the user login
     */
    public synchronized boolean isPasswordCorrect(String username,String receivedPassword) {
        String salt = this.userMap.get(username).getSalt();
        String savedPasswordHash = this.userMap.get(username).getHashedPassword();
        return PasswordUtils.isPasswordCorrect(receivedPassword,salt,savedPasswordHash);
    }

    /**
     * Adds the user to the system
     * @param username User's username
     * @param password User's password
     * @return false - User already exists or user registering failed,
     *         true - user has been added to the system
     */
    public synchronized boolean addUser(String username, String password)  {
        if ((findUser(username))!=null) {
            return false;
        }
        String salt = PasswordUtils.generateSalt();
        String hashedSaltedPassword = stringToHash(password,salt);
        User user = new User(username,salt,hashedSaltedPassword); // teeb kasutaja, TODO: võiks kontrollida paroolide sobivust
        this.userMap.put(username, user);
        appendUserToFile(user);
        return true;
    }


    /**
     * Adds the user to the file while the server is running (saves time on writing)
     * @param user User that is to be added to the file
     */
    private synchronized void appendUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath, StandardCharsets.UTF_8, true))) {
            writer.write(mapper.writeValueAsString(user));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to add user "+user.getUsername() + " data to file", e); // TODO
        }
    }


    /**
     * Saves user data into a file, should be run when the server shuts down.
     * Is atomatic, does not corrupt the user data file
     */
    public synchronized void saveUsersToFile() { // runs when the server shuts down and periodically
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
