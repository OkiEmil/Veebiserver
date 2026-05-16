import java.io.*;
import java.util.Properties;

public class ServerConfig {
    private static ServerConfig instance;
    private Properties properties;
    private final String CONFIG_FILE_PATH = "ServerConfig.ini";
    
    private int serverPort;
    private int sessionTimeout;
    private int saveInterval;
    private String publicRoot;
    private String logLevel;
    
    private ServerConfig(String configFilePath) {
        this.CONFIG_FILE_PATH = configFilePath;
        this.properties = new Properties();
        loadConfiguration();
    }
    
    public static synchronized ServerConfig getInstance(String configFilePath) {
        if (instance == null) {
            instance = new ServerConfig(configFilePath);
        }
        return instance;
    }
    
    private void loadConfiguration() {
        try (FileInputStream fileInput = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fileInput);
            parseProperties();
        } catch (IOException e) {
            System.err.println("Config file not found, using defaults: " + e.getMessage());
            setDefaults();
        }
    }
    
    private void parseProperties() {
        serverPort = Integer.parseInt(properties.getProperty("server.port", "8080"));
        sessionTimeout = Integer.parseInt(properties.getProperty("session.timeout", "1800")); // 30 min
        saveInterval = Integer.parseInt(properties.getProperty("save.interval", "600000")); // 10 min
        publicRoot = properties.getProperty("public.root", "public");
        logLevel = properties.getProperty("log.level", "INFO");
    }
    
    private void setDefaults() {
        serverPort = 8080;
        sessionTimeout = 1800;
        saveInterval = 600000;
        publicRoot = "public";
        logLevel = "INFO";
    }
    
    // Getters
    public int getServerPort() { return serverPort; }
    public int getSessionTimeout() { return sessionTimeout; }
    public int getSaveInterval() { return saveInterval; }
    public String getPublicRoot() { return publicRoot; }
    public String getLogLevel() { return logLevel; }
}