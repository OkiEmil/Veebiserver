import java.io.*;
import java.util.Properties;

public class ServerConfig {
    private static ServerConfig instance;
    private Properties properties;
    private String CONFIG_FILE_PATH = "ServerConfig.ini";
    
    private int serverPort;
    private int sessionTimeout;
    private int saveInterval;
    private String publicRoot;
    private String logLevel;
    
    private ServerConfig() {
        this.properties = new Properties();
        loadConfiguration();
    }
    
    public static synchronized ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
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

    public int readPropertyAsInt(String propertyName)
    {
        return Integer.parseInt(properties.getProperty(propertyName, "0"));
    }

    public double readPropertyAsDouble(String propertyName)
    {
        return Double.parseDouble(properties.getProperty(propertyName, "0"));
    }

    public String readPropertyAsString(String propertyName)
    {
        return properties.getProperty(propertyName, "");
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