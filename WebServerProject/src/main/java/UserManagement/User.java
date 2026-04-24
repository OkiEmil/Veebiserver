package UserManagement;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements Serializable {
    @JsonProperty("username")
    private String username;
    @JsonProperty("salt")
    private String salt;
    @JsonProperty("hashedPassword")
    private String hashedPassword;
    @JsonProperty("createdAt")
    private long createdAt;

    public User() {}

    public User(String username,String salt,String hashedPassword) {
        this.username=username;
        this.salt=salt;
        this.hashedPassword=hashedPassword;
        this.createdAt= System.currentTimeMillis();
    }
    public String getUsername() {
        return username;
    }

    public void setUserName(String userName) {
        this.username = userName;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getCreatedAt() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(createdAt));
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User "+ username;
    }
}
